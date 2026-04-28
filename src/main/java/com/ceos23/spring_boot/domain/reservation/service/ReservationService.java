package com.ceos23.spring_boot.domain.reservation.service;

import com.ceos23.spring_boot.domain.reservation.dto.ReservationCreateCommand;
import com.ceos23.spring_boot.domain.reservation.dto.ReservationInfo;
import com.ceos23.spring_boot.domain.reservation.entity.Reservation;
import com.ceos23.spring_boot.domain.reservation.entity.ReservationStatus;
import com.ceos23.spring_boot.domain.reservation.entity.ReservedSeat;
import com.ceos23.spring_boot.domain.reservation.entity.Schedule;
import com.ceos23.spring_boot.domain.reservation.repository.ReservationRepository;
import com.ceos23.spring_boot.domain.reservation.repository.ReservedSeatRepository;
import com.ceos23.spring_boot.domain.reservation.repository.ScheduleRepository;
import com.ceos23.spring_boot.domain.theater.entity.Seat;
import com.ceos23.spring_boot.domain.theater.repository.SeatRepository;
import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.domain.user.repository.UserRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservedSeatRepository reservedSeatRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReservationInfo createReservation(ReservationCreateCommand command) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(command.email())
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findByIdAndDeletedAtIsNull(command.scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        schedule.validateReservableTime(LocalDateTime.now());

        Long screenId = schedule.getScreen().getId();

        List<Seat> seats = getValidSeats(command.seatIds(), screenId);

        validateSeatNotReserved(schedule.getId(), command.seatIds());

        Reservation reservation = Reservation.create(user, schedule, seats);

        reservationRepository.save(reservation);

        String orderName = getOrderName(schedule, seats);

        return ReservationInfo.from(reservation, reservation.getReservedSeats(), orderName);
    }

    private List<Seat> getValidSeats(List<Long> seatIds, Long screenId) {
        List<Seat> seats = seatRepository.findAllByIdInAndScreenIdAndDeletedAtIsNull(seatIds, screenId);
        if (seats.size() != seatIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_SEAT);
        }
        return seats;
    }

    private void validateSeatNotReserved(Long scheduleId, List<Long> seatIds) {
        boolean isAlreadyOccupied = reservedSeatRepository.existsByScheduleIdAndSeatIdInAndReservationStatusIn(
                scheduleId,
                seatIds,
                List.of(ReservationStatus.PAID, ReservationStatus.PENDING)
        );

        if (isAlreadyOccupied) {
            throw new BusinessException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
    }

    private String getOrderName(Schedule schedule, List<Seat> seats) {
        String movieTitle = schedule.getMovie().getTitle();
        int seatCount = seats.size();

        return movieTitle + " " + seatCount + "매";
    }

    @Transactional
    public void confirmPayment(String paymentId) {
        Reservation reservation = reservationRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.completePayment();
    }

    @Transactional
    public void cancelReservation(String paymentId) {
        Reservation reservation = reservationRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.cancel();
    }

    public void verifyCancelable(String paymentId) {
        Reservation reservation = reservationRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.validateCancelable();
    }
}
