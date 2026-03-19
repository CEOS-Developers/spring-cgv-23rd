package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
