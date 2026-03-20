package com.ceos23.cgv_clone.reservation.service;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.common.codes.ErrorCode;
import com.ceos23.cgv_clone.common.codes.SuccessCode;
import com.ceos23.cgv_clone.config.exception.CustomException;
import com.ceos23.cgv_clone.movie.domain.Schedule;
import com.ceos23.cgv_clone.movie.repository.ScheduleRepository;
import com.ceos23.cgv_clone.reservation.domain.Reservation;
import com.ceos23.cgv_clone.reservation.domain.ReservationSeat;
import com.ceos23.cgv_clone.reservation.domain.ReservationStatus;
import com.ceos23.cgv_clone.reservation.dto.request.ReservationRequest;
import com.ceos23.cgv_clone.reservation.dto.response.ReservationResponse;
import com.ceos23.cgv_clone.reservation.repository.ReservationRepository;
import com.ceos23.cgv_clone.reservation.repository.ReservationSeatRepository;
import com.ceos23.cgv_clone.user.domain.User;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    // 영화 예매
    @Transactional
    public ApiResponse<ReservationResponse> createReservation(Long userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 1. 나이 검사
        if (user.getBirthdate() == null) {
            // 예외처리가 아닌, 생년월일 인증하는 방식으로 변경 필요.
            throw new CustomException(ErrorCode.USER_BIRTHDATE_NOT_FOUND);
        }
        int userAge = Period.between(user.getBirthdate(), LocalDate.now()).getYears();

        if (userAge < schedule.getMovie().getAgeRestriction()) {
            throw new CustomException(ErrorCode.AGE_RESTRICTED);
        }

        // 2. 좌석 검사
        for (String seatStr : request.getSeats()) {
            char row = seatStr.charAt(0);
            int col = Integer.parseInt(seatStr.substring(1));

            // 2-1. 좌석 문자열 검사
            if (seatStr == null || !seatStr.matches("^[A-Z]\\d+$")) {
                throw new CustomException(ErrorCode.INVALID_SEAT);
            }

            // 2-2. 좌석 범위 검사
            if (row > schedule.getScreen().getScreenType().getMaxRow() || col > schedule.getScreen().getScreenType().getMaxCol()) {
                throw new CustomException(ErrorCode.INVALID_SEAT);
            }

            // 2-3. 좌석 중복 검사
            if (reservationSeatRepository.existsByScheduleAndSeatRowAndSeatCol(schedule, row, col)) {
                throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
            }
        }

        int pricePerSeat = schedule.getScreen().getScreenType().getPrice();
        int totalPrice = pricePerSeat * request.getSeats().size();

        // 취소 이후에도 취소 내역 조회를 위한 스냅샷
        String snapshotSeatNames = String.join(", ", request.getSeats());

        Reservation reservation = Reservation.builder()
                .reservedAt(LocalDateTime.now())
                .totalPrice(totalPrice)
                .status(ReservationStatus.RESERVED)
                .user(user)
                .schedule(schedule)
                .seatNames(snapshotSeatNames)
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

        return ApiResponse.ok(SuccessCode.INSERT_SUCCESS, ReservationResponse.from(reservation));
    }

    // 예매 취소
    @Transactional
    public ApiResponse<Void> cancelReservation(Long userId, Long reservationId) {
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

        return ApiResponse.ok(SuccessCode.DELETE_SUCCESS);
    }
}
