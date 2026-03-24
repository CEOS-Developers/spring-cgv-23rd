package com.ceos23.spring_boot.cgv.repository.reservation;

import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}