package com.ceos23.cgv.domain.reservation.repository;

import com.ceos23.cgv.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
