package com.ceos23.cgv_clone.reservation.repository;

import com.ceos23.cgv_clone.theater.entity.Schedule;
import com.ceos23.cgv_clone.reservation.entity.ReservationSeat;
import com.ceos23.cgv_clone.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    boolean existsByScheduleAndSeatRowAndSeatColAndReservation_StatusNot(Schedule schedule, char row, int col, ReservationStatus reservationStatus);
}
