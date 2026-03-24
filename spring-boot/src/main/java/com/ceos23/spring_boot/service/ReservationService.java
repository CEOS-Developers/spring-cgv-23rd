package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.*;
import com.ceos23.spring_boot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;

    // 🎟 예매
    public Reservation reserve(Long userId, Long screeningId, Long seatId) {

        // 1. 좌석 중복 체크 (핵심🔥)
        boolean exists = reservationRepository
                .existsByScreeningIdAndSeatId(screeningId, seatId);

        if (exists) {
            throw new RuntimeException("이미 예약된 좌석입니다.");
        }

        // 2. 엔티티 조회
        User user = userRepository.findById(userId).orElseThrow();
        Screening screening = screeningRepository.findById(screeningId).orElseThrow();
        Seat seat = seatRepository.findById(seatId).orElseThrow();

        // 3. 저장
        Reservation reservation = new Reservation(user, screening, seat);
        return reservationRepository.save(reservation);
    }

    // ❌ 예매 취소
    @Transactional
    public void cancel(Long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new RuntimeException("예약 없음");
        }
        reservationRepository.deleteById(reservationId);
    }
}