package com.ceos23.spring_boot.cgv.service.reservation;

import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationSeat;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationStatus;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.global.exception.BadRequestException;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.cinema.SeatTemplateRepository;
import com.ceos23.spring_boot.cgv.repository.movie.ScreeningRepository;
import com.ceos23.spring_boot.cgv.repository.reservation.ReservationRepository;
import com.ceos23.spring_boot.cgv.repository.reservation.ReservationSeatRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import com.ceos23.spring_boot.cgv.service.payment.PaymentCreateCommand;
import com.ceos23.spring_boot.cgv.service.payment.PaymentService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private static final long STANDARD_TICKET_PRICE = 14_000L;
    private static final long PAYMENT_HOLD_MINUTES = 5L;

    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatTemplateRepository seatTemplateRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final PaymentService paymentService;

    @Transactional
    public Reservation createReservation(
            Long userId,
            Long screeningId,
            List<Long> seatTemplateIds
    ) {
        expireOverdueReservations();
        validateSeatRequest(seatTemplateIds);

        LocalDateTime now = LocalDateTime.now();
        User user = findUserById(userId);
        Screening screening = findScreeningByIdWithLock(screeningId);
        List<SeatTemplate> seatTemplates = findSeatTemplatesByIds(seatTemplateIds);

        validateSeatBelongsToScreening(screening, seatTemplates);
        validateAlreadyReserved(screening, seatTemplates, now);

        String paymentId = generatePaymentId();
        Reservation reservation = reservationRepository.save(
                new Reservation(user, screening, paymentId, now.plusMinutes(PAYMENT_HOLD_MINUTES))
        );
        saveReservationSeats(reservation, screening, seatTemplates);
        paymentService.startPayment(createPaymentCommand(paymentId, screening, seatTemplates));

        return reservation;
    }

    @Transactional
    public List<Reservation> getReservations(Long userId) {
        expireOverdueReservations();
        findUserById(userId);
        return reservationRepository.findAllByUserId(userId);
    }

    @Transactional
    public Reservation getReservation(Long reservationId, Long userId) {
        expireOverdueReservations();
        return findReservationByIdAndUserId(reservationId, userId);
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        expireOverdueReservations();
        Reservation reservation = findReservationByIdAndUserId(reservationId, userId);
        reservation.cancel(LocalDateTime.now());
        paymentService.cancelPayment(reservation.getPaymentId());
        releaseReservationSeats(reservation);
    }

    @Transactional
    public Reservation confirmPayment(Long reservationId, Long userId) {
        expireOverdueReservations();
        Reservation reservation = findReservationByIdAndUserId(reservationId, userId);
        reservation.confirmPayment(LocalDateTime.now());
        paymentService.completePayment(reservation.getPaymentId());
        return reservation;
    }

    public List<ReservationSeat> getReservationSeats(Reservation reservation) {
        return reservationSeatRepository.findAllByReservation(reservation);
    }

    @Transactional
    public void expireOverdueReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> overdueReservations = reservationRepository.findAllByStatusAndExpiresAtBefore(
                ReservationStatus.PENDING_PAYMENT,
                now
        );

        for (Reservation reservation : overdueReservations) {
            if (reservation.expire(now)) {
                paymentService.expirePayment(reservation.getPaymentId());
                releaseReservationSeats(reservation);
            }
        }
    }

    private void validateSeatRequest(List<Long> seatTemplateIds) {
        if (seatTemplateIds == null || seatTemplateIds.isEmpty()) {
            throw new BadRequestException(ErrorCode.EMPTY_SEAT_REQUEST);
        }

        validateDuplicateSeatRequest(seatTemplateIds);
    }

    private void validateDuplicateSeatRequest(List<Long> seatTemplateIds) {
        Set<Long> distinctSeatIds = new HashSet<>(seatTemplateIds);

        if (distinctSeatIds.size() != seatTemplateIds.size()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_SEAT_REQUEST);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Screening findScreeningByIdWithLock(Long screeningId) {
        return screeningRepository.findByIdWithPessimisticLock(screeningId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SCREENING_NOT_FOUND));
    }

    private Reservation findReservationByIdAndUserId(Long reservationId, Long userId) {
        return reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    private List<SeatTemplate> findSeatTemplatesByIds(List<Long> seatTemplateIds) {
        List<SeatTemplate> seatTemplates = seatTemplateRepository.findAllById(seatTemplateIds);

        if (seatTemplates.size() != seatTemplateIds.size()) {
            throw new NotFoundException(ErrorCode.SEAT_TEMPLATE_NOT_FOUND);
        }

        return seatTemplates;
    }

    private void validateSeatBelongsToScreening(Screening screening, List<SeatTemplate> seatTemplates) {
        Long screeningSeatLayoutId = screening.getSeatLayoutId();

        for (SeatTemplate seatTemplate : seatTemplates) {
            if (!seatTemplate.belongsTo(screeningSeatLayoutId)) {
                throw new BadRequestException(ErrorCode.INVALID_SEAT_FOR_SCREENING);
            }
        }
    }

    private PaymentCreateCommand createPaymentCommand(
            String paymentId,
            Screening screening,
            List<SeatTemplate> seatTemplates
    ) {
        return new PaymentCreateCommand(
                paymentId,
                screening.getMovie().getTitle() + " reservation",
                seatTemplates.size() * STANDARD_TICKET_PRICE,
                createSeatDetail(screening, seatTemplates)
        );
    }

    private String createSeatDetail(Screening screening, List<SeatTemplate> seatTemplates) {
        String seatNumbers = seatTemplates.stream()
                .map(SeatTemplate::getSeatNumber)
                .collect(Collectors.joining(","));

        return "screeningId=" + screening.getId() + ", seats=" + seatNumbers;
    }

    private String generatePaymentId() {
        return UUID.randomUUID().toString();
    }

    private List<ReservationSeat> createReservationSeats(
            Reservation reservation,
            Screening screening,
            List<SeatTemplate> seatTemplates
    ) {
        return seatTemplates.stream()
                .map(seatTemplate -> new ReservationSeat(reservation, screening, seatTemplate))
                .toList();
    }

    private void saveReservationSeats(
            Reservation reservation,
            Screening screening,
            List<SeatTemplate> seatTemplates
    ) {
        try {
            reservationSeatRepository.saveAllAndFlush(
                    createReservationSeats(reservation, screening, seatTemplates)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(ErrorCode.ALREADY_RESERVED_SEAT);
        }
    }

    private void validateAlreadyReserved(
            Screening screening,
            List<SeatTemplate> seatTemplates,
            LocalDateTime now
    ) {
        List<Long> reservedSeatTemplateIds =
                reservationSeatRepository.findActiveSeatTemplateIdsByScreeningAndSeatTemplates(
                        screening,
                        seatTemplates,
                        ReservationStatus.CONFIRMED,
                        ReservationStatus.PENDING_PAYMENT,
                        now
                );

        if (!reservedSeatTemplateIds.isEmpty()) {
            throw new ConflictException(ErrorCode.ALREADY_RESERVED_SEAT);
        }
    }

    private void releaseReservationSeats(Reservation reservation) {
        reservationSeatRepository.deleteAllByReservation(reservation);
        reservationSeatRepository.flush();
    }
}
