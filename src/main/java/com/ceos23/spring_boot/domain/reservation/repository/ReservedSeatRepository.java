package com.ceos23.spring_boot.domain.reservation.repository;

import com.ceos23.spring_boot.domain.reservation.entity.ReservationStatus;
import com.ceos23.spring_boot.domain.reservation.entity.ReservedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
    boolean existsByScheduleIdAndSeatIdInAndReservationStatusIn(Long scheduleId, List<Long> seatIds, List<ReservationStatus> status);
}
