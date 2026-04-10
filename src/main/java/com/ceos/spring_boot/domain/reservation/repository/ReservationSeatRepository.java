package com.ceos.spring_boot.domain.reservation.repository;

import com.ceos.spring_boot.domain.reservation.entity.ReservationSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    // 특정 상영 일정에 이미 예약 완료된 좌석 목록 조회
    @Query("SELECT rs.seat.id FROM ReservationSeat rs WHERE rs.reservation.schedule.id = :scheduleId AND rs.reservation.status = 'CONFIRMED'")
    List<Long> findReservedSeatIdsByScheduleId(@Param("scheduleId") Long scheduleId);

    // 비관적 락 적용: 조회하는 동안 다른 트랜잭션이 해당 행을 수정/삭제/조회하지 못하게 함 -> 나중에 Redisson 방식(분산 락)으로 바꾸는게..
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rs FROM ReservationSeat rs " +
            "WHERE rs.reservation.schedule.id = :scheduleId " +
            "AND rs.seat.id = :seatId " +
            "AND rs.reservation.status = 'CONFIRMED'")
    Optional<ReservationSeat> findByScheduleIdAndSeatIdWithLock(@Param("scheduleId") Long scheduleId, @Param("seatId") Long seatId);

    // 특정 상영 일정에 해당 좌석이 이미 있는지 확인
    boolean existsByScheduleIdAndSeatId(Long scheduleId, Long seatId);

    // 취소 시 사용되는 삭제 메서드
    void deleteByReservationId(Long reservationId);
}
