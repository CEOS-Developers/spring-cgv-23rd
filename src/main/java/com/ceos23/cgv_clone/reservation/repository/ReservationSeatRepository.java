package com.ceos23.cgv_clone.reservation.repository;

import com.ceos23.cgv_clone.reservation.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
}
