package com.cgv.spring_boot.domain.reservation.service;

import com.cgv.spring_boot.domain.reservation.dto.ReservationRequest;
import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.domain.reservation.entity.ReservationStatus;
import com.cgv.spring_boot.domain.reservation.entity.ReservedSeat;
import com.cgv.spring_boot.domain.reservation.repository.ReservationRepository;
import com.cgv.spring_boot.domain.reservation.repository.ReservedSeatRepository;
import com.cgv.spring_boot.domain.schedule.entity.Schedule;
import com.cgv.spring_boot.domain.schedule.repository.ScheduleRepository;
import com.cgv.spring_boot.domain.theater.entity.HallType;
import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.domain.reservation.exception.ReservationErrorCode;
import com.cgv.spring_boot.domain.schedule.exception.ScheduleErrorCode;
import com.cgv.spring_boot.domain.user.exception.UserErrorCode;
import com.cgv.spring_boot.global.error.code.GlobalErrorCode;
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
    @Transactional
    public Long reserve(Long userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(request.scheduleId())
                .orElseThrow(() -> new BusinessException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        validateSeatRange(schedule.getHall().getHallType(), request.seats());

        List<SeatRequest> normalizedSeats = request.seats().stream()
                .map(seatRequest -> new SeatRequest(normalizeSeatRow(seatRequest.seatRow()), seatRequest.seatCol()))
                .toList();

        List<String> rows = normalizedSeats.stream().map(SeatRequest::seatRow).toList();
        List<Integer> cols = request.seats().stream().map(SeatRequest::seatCol).toList();

        if (!reservedSeatRepository.findAllByScheduleAndRowsAndCols(schedule.getId(), rows, cols).isEmpty()) {
            throw new BusinessException(ReservationErrorCode.ALREADY_RESERVED_SEAT);
        }

        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.RESERVED)
                .user(user)
                .schedule(schedule)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        List<ReservedSeat> reservedSeats = normalizedSeats.stream()
                .map(seatRequest -> ReservedSeat.builder()
                        .seatRow(seatRequest.seatRow())
                        .seatCol(seatRequest.seatCol())
                        .schedule(schedule) // 유니크 제약 조건을 위해 반드시 추가!
                        .reservation(savedReservation)
                        .build())
                .toList();

        reservedSeatRepository.saveAll(reservedSeats);

        return savedReservation.getId();
    }

    private void validateSeatRange(HallType hallType, List<SeatRequest> seats) {
        for (SeatRequest seat : seats) {
            if (isInvalidSeatRow(seat.seatRow(), hallType.getRowCount())
                    || seat.seatCol() < 1
                    || seat.seatCol() > hallType.getColCount()) {
                throw new BusinessException(ReservationErrorCode.INVALID_SEAT_POSITION);
            }
        }
    }

    private boolean isInvalidSeatRow(String seatRow, int rowCount) {
        if (seatRow == null || seatRow.length() != 1) {
            return true;
        }

        char row = Character.toUpperCase(seatRow.charAt(0));
        char maxRow = (char) ('A' + rowCount - 1);

        return row < 'A' || row > maxRow;
    }

    private String normalizeSeatRow(String seatRow) {
        return String.valueOf(Character.toUpperCase(seatRow.charAt(0)));
    }

    /**
     * 예매 취소 메서드
     */
    @Transactional
    public void cancel(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessException(GlobalErrorCode.FORBIDDEN_ACCESS);
        }

        reservation.cancelStatus();

        reservedSeatRepository.deleteByReservation(reservation);
    }
}
