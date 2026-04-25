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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            List<Long> seatTemplateIds,
            String paymentId
    ) {
        validateSeatRequest(seatTemplateIds);

        User user = findUserById(userId);
        Screening screening = findScreeningByIdWithLock(screeningId);
        List<SeatTemplate> seatTemplates = findSeatTemplatesByIds(seatTemplateIds);

        validateSeatBelongsToScreening(screening, seatTemplates);
        validateAlreadyReserved(screening, seatTemplates);

        paymentService.requestPayment(createPaymentCommand(paymentId, screening, seatTemplates));

        Reservation reservation = reservationRepository.save(new Reservation(user, screening, paymentId));
        saveReservationSeats(reservation, screening, seatTemplates);

        return reservation;
    }

    public List<Reservation> getReservations(Long userId) {
        findUserById(userId);
        return reservationRepository.findAllByUserId(userId);
    }

    public Reservation getReservation(Long reservationId, Long userId) {
        return findReservationByIdAndUserId(reservationId, userId);
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = findReservationByIdAndUserId(reservationId, userId);

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new ConflictException(ErrorCode.ALREADY_CANCELED_RESERVATION);
        }

        paymentService.cancelPayment(reservation.getPaymentId());
        reservation.cancel();
    }

    public List<ReservationSeat> getReservationSeats(Reservation reservation) {
        return reservationSeatRepository.findAllByReservation(reservation);
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

    private void validateAlreadyReserved(Screening screening, List<SeatTemplate> seatTemplates) {
        List<Long> reservedSeatTemplateIds =
                reservationSeatRepository.findReservedSeatTemplateIdsByScreeningAndSeatTemplates(
                        screening,
                        seatTemplates,
                        ReservationStatus.RESERVED
                );

        if (!reservedSeatTemplateIds.isEmpty()) {
            throw new ConflictException(ErrorCode.ALREADY_RESERVED_SEAT);
        }
    }
}
