package com.cgv.spring_boot.domain.reservation.service;

import com.cgv.spring_boot.domain.reservation.dto.ReservationRequest;
import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.domain.reservation.entity.ReservationStatus;
import com.cgv.spring_boot.domain.reservation.entity.ReservedSeat;
import com.cgv.spring_boot.domain.reservation.repository.ReservationRepository;
import com.cgv.spring_boot.domain.reservation.repository.ReservedSeatRepository;
import com.cgv.spring_boot.domain.schedule.entity.Schedule;
import com.cgv.spring_boot.domain.schedule.repository.ScheduleRepository;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.global.common.code.ErrorCode;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cgv.spring_boot.domain.reservation.dto.ReservationRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ReservedSeatRepository reservedSeatRepository;

    /**
     * 영화 예매 메서드
     */
    public Long reserve(Long userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(request.scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        for (SeatRequest seat : request.seats()) {
            boolean isAlreadyTaken = reservedSeatRepository.existsByScheduleAndRowAndCol(
                    schedule.getId(), seat.seatRow(), seat.seatCol()
            );

            if (isAlreadyTaken) {
                throw new BusinessException(ErrorCode.ALREADY_RESERVED_SEAT);
            }
        }

        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.RESERVED)
                .user(user)
                .schedule(schedule)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        List<ReservedSeat> reservedSeats = request.seats().stream()
                .map(seatRequest -> ReservedSeat.builder()
                        .seatRow(seatRequest.seatRow())
                        .seatCol(seatRequest.seatCol())
                        .reservation(savedReservation)
                        .build())
                .toList();

        reservedSeatRepository.saveAll(reservedSeats);

        return savedReservation.getId();
    }

    /**
     * 예매 취소 메서드
     */
    public void cancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELED);
        }

        reservation.cancelStatus();

        reservedSeatRepository.deleteByReservation(reservation);
    }
}
