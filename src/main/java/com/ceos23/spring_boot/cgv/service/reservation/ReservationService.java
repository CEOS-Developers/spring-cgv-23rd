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
@Transactional
public class ReservationService {

    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatTemplateRepository seatTemplateRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    public Reservation createReservation(Long userId, Long screeningId, List<Long> seatTemplateIds) {
        if (seatTemplateIds == null || seatTemplateIds.isEmpty()) {
            throw new BadRequestException(ErrorCode.EMPTY_SEAT_REQUEST);
        }

        validateDuplicateSeatRequest(seatTemplateIds);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        "해당 사용자가 존재하지 않습니다. id=" + userId
                ));

        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.SCREENING_NOT_FOUND,
                        "해당 상영 정보가 존재하지 않습니다. id=" + screeningId
                ));

        List<SeatTemplate> seatTemplates = seatTemplateRepository.findAllById(seatTemplateIds);

        if (seatTemplates.size() != seatTemplateIds.size()) {
            throw new NotFoundException(ErrorCode.SEAT_TEMPLATE_NOT_FOUND);
        }

        Long screeningSeatLayoutId = screening.getScreen().getSeatLayout().getId();

        for (SeatTemplate seatTemplate : seatTemplates) {
            validateSeatBelongsToScreening(screeningSeatLayoutId, seatTemplate);
        }

        validateAlreadyReserved(screening, seatTemplates);

        Reservation reservation = new Reservation(user, screening);
        Reservation savedReservation = reservationRepository.save(reservation);

        List<ReservationSeat> reservationSeats = seatTemplates.stream()
                .map(seatTemplate -> new ReservationSeat(savedReservation, screening, seatTemplate))
                .toList();

        reservationSeatRepository.saveAll(reservationSeats);

        return savedReservation;
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        "해당 사용자가 존재하지 않습니다. id=" + userId
                ));

        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getUser().getId().equals(user.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Reservation getReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "해당 예매가 존재하지 않습니다. id=" + reservationId
                ));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new NotFoundException(
                    ErrorCode.RESERVATION_NOT_FOUND,
                    "해당 예매가 존재하지 않습니다. id=" + reservationId
            );
        }

        return reservation;
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "해당 예매가 존재하지 않습니다. id=" + reservationId
                ));

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new ConflictException(
                    ErrorCode.ALREADY_CANCELED_RESERVATION,
                    "이미 취소된 예매입니다. id=" + reservationId
            );
        }

        reservation.cancel();
    }

    @Transactional(readOnly = true)
    public List<ReservationSeat> getReservationSeats(Reservation reservation) {
        return reservationSeatRepository.findAllByReservation(reservation);
    }

    private void validateDuplicateSeatRequest(List<Long> seatTemplateIds) {
        Set<Long> distinctSeatIds = new HashSet<>(seatTemplateIds);

        if (distinctSeatIds.size() != seatTemplateIds.size()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_SEAT_REQUEST);
        }
    }

    private void validateSeatBelongsToScreening(Long screeningSeatLayoutId, SeatTemplate seatTemplate) {
        if (!seatTemplate.getSeatLayout().getId().equals(screeningSeatLayoutId)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_SEAT_FOR_SCREENING,
                    "해당 상영관에서 선택할 수 없는 좌석입니다. seatTemplateId=" + seatTemplate.getId()
            );
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
            throw new ConflictException(
                    ErrorCode.ALREADY_RESERVED_SEAT,
                    "이미 예매된 좌석입니다. seatTemplateIds=" + reservedSeatTemplateIds
            );
        }
    }
}