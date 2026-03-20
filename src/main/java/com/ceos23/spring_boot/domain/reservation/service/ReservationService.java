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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservedSeatRepository reservedSeatRepository;

    @Transactional
    public ReservationInfo createReservation(ReservationCreateCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(command.scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        List<Seat> seats = seatRepository.findAllById(command.seatIds());
        if (seats.size() != command.seatIds().size()) {
            throw new BusinessException(ErrorCode.SEAT_NOT_FOUND);
        }

        boolean isAlreadyReserved = reservedSeatRepository.existsByScheduleIdAndSeatIdInAndReservationStatus(
                schedule.getId(),
                command.seatIds(),
                ReservationStatus.RESERVED
        );

        if (isAlreadyReserved) {
            throw new BusinessException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .schedule(schedule)
                .status(ReservationStatus.RESERVED)
                .build();
        reservationRepository.save(reservation);

        List<ReservedSeat> reservedSeats = seats.stream()
                .map(seat -> ReservedSeat.builder()
                        .reservation(reservation)
                        .seat(seat)
                        .build())
                .toList();
        reservedSeatRepository.saveAll(reservedSeats);

        return ReservationInfo.from(reservation, reservedSeats);

    }

    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_RESERVATION_ACCESS);
        }

        reservation.cancel();
    }
}
