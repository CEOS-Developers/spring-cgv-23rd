package com.ceos.spring_cgv_23rd.domain.reservation.repository;

import com.ceos.spring_cgv_23rd.domain.reservation.entity.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    // 특정 상영의 예약된 좌석 목록 조회
    @Query("SELECT rs.seat.id FROM ReservationSeat rs " +
            "WHERE rs.reservation.screening.id = :screeningId " +
            "AND rs.reservation.status = 'COMPLETED'")
    List<Long> findReservedSeatIdsByScreeningId(Long screeningId);

    List<ReservationSeat> findByReservationId(Long reservationId);
}
