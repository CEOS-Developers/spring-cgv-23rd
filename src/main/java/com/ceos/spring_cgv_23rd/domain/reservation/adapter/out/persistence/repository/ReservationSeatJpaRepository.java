package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.persistence.entity.ReservationSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationSeatJpaRepository extends JpaRepository<ReservationSeatEntity, Long> {

    // 특정 상영의 예약된 좌석 목록 조회
    @Query("SELECT rs.seatId FROM ReservationSeatEntity rs " +
            "WHERE rs.reservation.screeningId = :screeningId " +
            "AND rs.reservation.status = 'COMPLETED'")
    List<Long> findReservedSeatIdsByScreeningId(Long screeningId);

    List<ReservationSeatEntity> findByReservationId(Long reservationId);
}
