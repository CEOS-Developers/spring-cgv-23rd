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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatTemplateRepository seatTemplateRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    @Transactional
    public Reservation createReservation(Long userId, Long screeningId, List<Long> seatTemplateIds) {
        validateSeatRequest(seatTemplateIds);

        User user = findUserById(userId);
        Screening screening = findScreeningById(screeningId);
        List<SeatTemplate> seatTemplates = findSeatTemplatesByIds(seatTemplateIds);

        validateSeatBelongsToScreening(screening, seatTemplates);
        validateAlreadyReserved(screening, seatTemplates);

        Reservation reservation = reservationRepository.save(new Reservation(user, screening));
        reservationSeatRepository.saveAll(createReservationSeats(reservation, screening, seatTemplates));

        return reservation;
    }

    public List<Reservation> getReservations(Long userId) {
        findUserById(userId);
        return reservationRepository.findAllByUserId(userId);
    }

    public Reservation getReservation(Long reservationId, Long userId) {
        return reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        findReservationById(reservationId).cancel();
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

    private Screening findScreeningById(Long screeningId) {
        return screeningRepository.findById(screeningId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SCREENING_NOT_FOUND));
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
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

    private List<ReservationSeat> createReservationSeats(
            Reservation reservation,
            Screening screening,
            List<SeatTemplate> seatTemplates
    ) {
        return seatTemplates.stream()
                .map(seatTemplate -> new ReservationSeat(reservation, screening, seatTemplate))
                .toList();
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
