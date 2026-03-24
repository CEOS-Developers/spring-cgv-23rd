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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
    public ReservationResponse createReservation(Long userId, ReservationRequest request) { // 1. 파라미터를 request로 변경

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Schedule schedule = scheduleRepository.findById(request.scheduleId()) // 2. request에서 ID 가져오기
                .orElseThrow(() -> new EntityNotFoundException("상영 일정을 찾을 수 없습니다."));

        ScreenType currentType = schedule.getScreen().getScreenType();

        // 3. 좌표로 좌석 엔티티 조회
        List<Seat> seats = request.seats().stream()
                .map(seatReq -> {
                    // 사용자가 소문자 'a'를 입력해도 'A'로 처리되도록 대문자 변환
                    String rowUpper = seatReq.row().toUpperCase();

                    // 타입 + 행 + 열로 좌석 조회
                    return seatRepository.findByScreenTypeAndSeatRowAndSeatCol(currentType, rowUpper, seatReq.column())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "좌석을 찾을 수 없습니다: "
                            ));
                })
                .toList();

        // 4. 좌석 중복 검증 (찾아온 seats 리스트를 활용)
        for (Seat seat : seats) {
            Optional<ReservationSeat> reservedSeat =
                    reservationSeatRepository.findByScheduleIdAndSeatIdWithLock(schedule.getId(), seat.getId());

            if (reservedSeat.isPresent()) {
                throw new IllegalStateException("이미 선택된 좌석입니다: " + seat.getSeatRow() + "-" + seat.getSeatCol());
            }
        }

        // 5. 예매 생성 및 저장
        Reservation reservation = Reservation.builder()
                .user(user)
                .schedule(schedule)
                .reservationDate(LocalDateTime.now())
                .status(ReservationStatus.CONFIRMED)
                .build();
        reservationRepository.save(reservation);

        // 6. 예매-좌석 연결 저장
        List<ReservationSeat> reservationSeats = seats.stream()
                .map(seat -> {
                    ReservationSeat rs = ReservationSeat.builder()
                            .reservation(reservation)
                            .seat(seat)
                            .build();
                    reservation.addReservationSeat(rs);
                    return rs;
                })
                .toList();
        reservationSeatRepository.saveAll(reservationSeats);

        return ReservationResponse.from(reservation);
    }

    // 취소하기
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 예매입니다.");
        }

        // 예매 상태를 취소로 변경
        reservation.cancel();

        // 연결된 좌석 데이터 처리
        reservationSeatRepository.deleteByReservationId(reservationId);
    }
}
