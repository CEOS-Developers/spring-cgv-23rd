package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.reservation.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
}
