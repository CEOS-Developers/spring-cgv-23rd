package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.*;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
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
    @Transactional
    public Reservation reserve(Long userId, Long screeningId, Long seatId) {

        // 0. 잘못된 요청값 체크
        if (userId <= 0 || screeningId <= 0 || seatId <= 0) {
            throw new IllegalArgumentException("id는 양수여야 합니다.");
        }

        // 1. 좌석 중복 체크
        boolean exists = reservationRepository
                .existsByScreeningIdAndSeatId(screeningId, seatId);

        if (exists) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        // 2. 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCREENING_NOT_FOUND));

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));

        // 3. 저장
        Reservation reservation = new Reservation(user, screening, seat);
        return reservationRepository.save(reservation);
    }

    // ❌ 예매 취소
    @Transactional
    public void cancel(Long reservationId) {
        if (reservationId <= 0) {
            throw new IllegalArgumentException("id는 양수여야 합니다.");
        }

        if (!reservationRepository.existsById(reservationId)) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        reservationRepository.deleteById(reservationId);
    }
}