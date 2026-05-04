package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.Reservation;
import com.ceos23.spring_boot.domain.Screening;
import com.ceos23.spring_boot.domain.Seat;
import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.infra.payment.PaymentGateway;
import com.ceos23.spring_boot.infra.payment.dto.PaymentData;
import com.ceos23.spring_boot.repository.ReservationRepository;
import com.ceos23.spring_boot.repository.ScreeningRepository;
import com.ceos23.spring_boot.repository.SeatRepository;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final int TICKET_PRICE = 15000;
    private static final DateTimeFormatter PAYMENT_ID_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final PaymentGateway paymentGateway;
    private final ReservationTransactionService reservationTransactionService;

    @Transactional
    public Reservation reserve(Long userId, Long screeningId, Long seatId) {
        ensureSeatNotReserved(screeningId, seatId);

        User user = loadUser(userId);
        Screening screening = loadScreening(screeningId);
        Seat seat = loadSeat(seatId);

        Reservation reservation = Reservation.create(user, screening, seat);

        try {
            return reservationRepository.saveAndFlush(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
    }

    public Reservation payReservation(Long reservationId) {
        Reservation reservation = loadReservation(reservationId);

        if (reservation.isExpired(LocalDateTime.now())) {
            reservationTransactionService.deleteReservation(reservationId);
            throw new CustomException(ErrorCode.RESERVATION_EXPIRED);
        }

        if (reservation.isPaid()) {
            return reservation;
        }

        String paymentId = createPaymentId(reservation.getId());

        try {
            PaymentData payment = paymentGateway.pay(
                    paymentId,
                    createOrderName(reservation),
                    TICKET_PRICE,
                    createCustomData(reservation)
            );

            return reservationTransactionService.markPaid(
                    reservationId,
                    paymentId,
                    resolvePaidAt(payment)
            );

        } catch (CustomException e) {
            return handlePaymentException(e, reservationId, reservation, paymentId);
        }
    }

    @Transactional
    public void cancel(Long reservationId) {
        Reservation reservation = loadReservation(reservationId);

        if (reservation.isPaid()) {
            paymentGateway.cancel(reservation.getPaymentId());
        }

        reservationRepository.delete(reservation);
    }

    private Reservation handlePaymentException(
            CustomException e,
            Long reservationId,
            Reservation reservation,
            String paymentId
    ) {
        if (e.getErrorCode() == ErrorCode.PAYMENT_SERVER_ERROR) {
            return retryPayment(reservationId, reservation, paymentId);
        }

        if (e.getErrorCode() == ErrorCode.PAYMENT_CONFLICT) {
            PaymentData payment = paymentGateway.getPayment(paymentId);

            return reservationTransactionService.markPaid(
                    reservationId,
                    paymentId,
                    resolvePaidAt(payment)
            );
        }

        reservationTransactionService.deleteReservation(reservationId);
        throw e;
    }

    private Reservation retryPayment(Long reservationId, Reservation reservation, String paymentId) {
        try {
            PaymentData payment = paymentGateway.pay(
                    paymentId,
                    createOrderName(reservation),
                    TICKET_PRICE,
                    createCustomData(reservation)
            );

            return reservationTransactionService.markPaid(
                    reservationId,
                    paymentId,
                    resolvePaidAt(payment)
            );

        } catch (CustomException retryException) {
            reservationTransactionService.deleteReservation(reservationId);
            throw new CustomException(ErrorCode.PAYMENT_RETRY_FAILED);
        }
    }

    private void ensureSeatNotReserved(Long screeningId, Long seatId) {
        boolean exists = reservationRepository.existsByScreeningIdAndSeatId(screeningId, seatId);

        if (exists) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
    }

    private Reservation loadReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Screening loadScreening(Long screeningId) {
        return screeningRepository.findById(screeningId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCREENING_NOT_FOUND));
    }

    private Seat loadSeat(Long seatId) {
        return seatRepository.findWithLockById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
    }

    private String createPaymentId(Long reservationId) {
        return "RSV_" + reservationId + "_" + LocalDateTime.now().format(PAYMENT_ID_FORMATTER);
    }

    private String createOrderName(Reservation reservation) {
        return "CGV 좌석 예매 #" + reservation.getId();
    }

    private String createCustomData(Reservation reservation) {
        return "RESERVATION:" + reservation.getId();
    }

    private LocalDateTime resolvePaidAt(PaymentData payment) {
        if (payment.paidAt() != null) {
            return payment.paidAt();
        }

        return LocalDateTime.now();
    }
}