package com.ceos23.spring_boot.repository;

import com.ceos23.spring_boot.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 좌석 중복 예약 체크
    boolean existsByScreeningIdAndSeatId(Long screeningId, Long seatId);
}