package com.ceos23.cgv_clone.reservation.repository;

import com.ceos23.cgv_clone.movie.domain.Schedule;
import com.ceos23.cgv_clone.reservation.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    boolean existsByScheduleAndSeatRowAndSeatCol(Schedule schedule, char row, int col);
}
