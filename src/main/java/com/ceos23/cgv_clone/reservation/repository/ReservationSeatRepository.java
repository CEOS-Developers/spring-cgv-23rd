package com.ceos23.cgv_clone.reservation.repository;

import com.ceos23.cgv_clone.movie.domain.Schedule;
import com.ceos23.cgv_clone.reservation.domain.ReservationSeat;
import com.ceos23.cgv_clone.reservation.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    boolean existsByScheduleAndSeatRowAndSeatCol_StatusNot(Schedule schedule, char row, int col, ReservationStatus reservationStatus);
}
