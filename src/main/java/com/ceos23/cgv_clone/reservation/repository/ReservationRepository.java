package com.ceos23.cgv_clone.reservation.repository;

import com.ceos23.cgv_clone.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
