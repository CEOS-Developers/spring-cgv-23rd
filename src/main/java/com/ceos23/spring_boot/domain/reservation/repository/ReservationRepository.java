package com.ceos23.spring_boot.domain.reservation.repository;

import com.ceos23.spring_boot.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
