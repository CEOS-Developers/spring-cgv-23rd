package com.ceos23.cgv_clone.reservation.service;

import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.theater.entity.Schedule;
import com.ceos23.cgv_clone.theater.repository.ScheduleRepository;
import com.ceos23.cgv_clone.reservation.entity.Reservation;
import com.ceos23.cgv_clone.reservation.entity.ReservationSeat;
import com.ceos23.cgv_clone.reservation.entity.ReservationStatus;
import com.ceos23.cgv_clone.reservation.dto.request.ReservationRequest;
import com.ceos23.cgv_clone.reservation.dto.response.ReservationResponse;
import com.ceos23.cgv_clone.reservation.repository.ReservationRepository;
import com.ceos23.cgv_clone.reservation.repository.ReservationSeatRepository;
import com.ceos23.cgv_clone.user.entity.User;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceBasic{

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    // 영화 예매
    @Transactional
    public ReservationResponse createReservation(Long userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        List<String> seats = request.getSeats();

        validateAgeRestriction(user, schedule.getMovie().getAgeRestriction());
        validateSeats(seats, schedule);

        int totalPrice = schedule.getScreen().getScreenType().getPrice() * request.getSeats().size();

        Reservation reservation = Reservation.builder()
                .reservedAt(LocalDateTime.now())
                .totalPrice(totalPrice)
                .status(ReservationStatus.RESERVED)
                .user(user)
                .schedule(schedule)
                .build();

        for (String seatStr : request.getSeats()) {
            ReservationSeat seat = ReservationSeat.builder()
                    .seatRow(seatStr.charAt(0))
                    .seatCol(Integer.parseInt(seatStr.substring(1)))
                    .reservation(reservation)
                    .schedule(schedule)
                    .build();
            reservation.addReservationSeat(seat);
        }

        reservationRepository.save(reservation);
        return ReservationResponse.from(reservation);
    }

    // 예매 취소
    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 1. 예매했던 id랑 사용자 id 일치 확인
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ERROR);
        }

        // 2. 예매 내역이 취소상태인지 확인
        if (reservation.getStatus().equals(ReservationStatus.CANCELED)) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_RESERVATION);
        }

        reservation.cancel();
    }

    private void validateAgeRestriction(User user, int ageRestriction) {
        if (ageRestriction < 18) return;

        if (user.getBirthdate() == null) {
            throw new CustomException(ErrorCode.USER_BIRTHDATE_NOT_FOUND);
        }

        int userAge = Period.between(user.getBirthdate(), LocalDate.now()).getYears();
        if (userAge < ageRestriction) {
            throw new CustomException(ErrorCode.AGE_RESTRICTED);
        }
    }

    private void validateSeats(List<String> seats, Schedule schedule) {
        if (seats.size() != new HashSet<>(seats).size()) {
            throw new CustomException(ErrorCode.INVALID_SEAT);
        }

        for (String seatStr : seats) {
            // 2-2. 좌석 문자열 검사
            if (seatStr == null || !seatStr.matches("^[A-Z]\\d+$")) {
                throw new CustomException(ErrorCode.INVALID_SEAT);
            }

            char row = seatStr.charAt(0);
            int col = Integer.parseInt(seatStr.substring(1));

            // 2-3. 좌석 범위 검사
            if (row > schedule.getScreen().getScreenType().getMaxRow() || col > schedule.getScreen().getScreenType().getMaxCol()) {
                throw new CustomException(ErrorCode.INVALID_SEAT);
            }

            // 2-4. 좌석 중복 검사
            // ReservationStatus.Canceled가 아닌 것 조회 -> 즉 취소 된 거는 카운트 X
            if (reservationSeatRepository.existsByScheduleAndSeatRowAndSeatColAndReservation_StatusNot(schedule, row, col, ReservationStatus.CANCELED)) {
                throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
            }
        }
    }
}
