package com.ceos.spring_boot.domain.reservation.service;

import com.ceos.spring_boot.domain.cinema.entity.ScreenType;
import com.ceos.spring_boot.domain.cinema.entity.Seat;
import com.ceos.spring_boot.domain.cinema.repository.SeatRepository;
import com.ceos.spring_boot.domain.reservation.dto.ReservationRequest;
import com.ceos.spring_boot.domain.reservation.dto.ReservationResponse;
import com.ceos.spring_boot.domain.reservation.entity.Reservation;
import com.ceos.spring_boot.domain.reservation.entity.ReservationSeat;
import com.ceos.spring_boot.domain.reservation.entity.ReservationStatus;
import com.ceos.spring_boot.domain.reservation.repository.ReservationRepository;
import com.ceos.spring_boot.domain.reservation.repository.ReservationSeatRepository;
import com.ceos.spring_boot.domain.schedule.entity.Schedule;
import com.ceos.spring_boot.domain.schedule.repository.ScheduleRepository;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.domain.user.repository.UserRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    // 예매
    @Transactional
    public ReservationResponse createReservation(Long userId, ReservationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND_ERROR.getMessage()));

        Schedule schedule = scheduleRepository.findById(request.scheduleId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.SCHEDULE_NOT_FOUND_ERROR.getMessage()));

        ScreenType currentType = schedule.getScreen().getScreenType();

        // 좌표로 좌석 엔티티 조회
        List<Seat> seats = request.seats().stream()
                .map(seatReq -> {
                    // 사용자가 소문자 'a'를 입력해도 'A'로 처리되도록 대문자 변환
                    String rowUpper = seatReq.row().toUpperCase();

                    // 타입 + 행 + 열로 좌석 조회
                    return seatRepository.findByScreenTypeAndSeatRowAndSeatCol(currentType, rowUpper, seatReq.column())
                            .orElseThrow(() -> new IllegalArgumentException(ErrorCode.SEAT_NOT_FOUND_ERROR.getMessage()));
                })
                .toList();

        // 좌석 중복 검증 (찾아온 seats 리스트를 활용)
        for (Seat seat : seats) {
            if (reservationSeatRepository.existsByScheduleIdAndSeatId(schedule.getId(), seat.getId())) {
                throw new IllegalStateException(ErrorCode.ALREADY_RESERVED_SEAT_ERROR.getMessage());
            }
        }


        try {
            // 예매 생성 및 저장
            Reservation reservation = Reservation.builder()
                    .user(user)
                    .schedule(schedule)
                    .status(ReservationStatus.CONFIRMED)
                    .build();
            reservationRepository.save(reservation);

            // 예매-좌석 연결 저장
            List<ReservationSeat> reservationSeats = seats.stream()
                    .map(seat -> {
                        ReservationSeat rs = ReservationSeat.builder()
                                .reservation(reservation)
                                .seat(seat)
                                .schedule(schedule) // 반드시 포함
                                .build();
                        reservation.addReservationSeat(rs);
                        return rs;
                    })
                    .toList();

            reservationSeatRepository.saveAll(reservationSeats);

            // flush를 호출하여 트랜잭션 종료 전 DB 제약 조건 위반을 즉시 확인
            reservationSeatRepository.flush();

            return ReservationResponse.from(reservation);

        } catch (DataIntegrityViolationException e) {
            // 두 명의 유저가 동시에 insert를 시도하여 DB Unique 제약 조건이 발동한 경우
            throw new IllegalStateException(ErrorCode.ALREADY_RESERVED_SEAT_ERROR.getMessage());
        }
    }

    // 취소하기
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.RESERVATION_NOT_FOUND_ERROR.getMessage()));

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalStateException(ErrorCode.ALREADY_CANCELED_RESERVATION_ERROR.getMessage());
        }

        // 예매 상태를 취소로 변경
        reservation.cancel();

        // 연결된 좌석 데이터 처리
        reservationSeatRepository.deleteByReservationId(reservationId);
    }
}
