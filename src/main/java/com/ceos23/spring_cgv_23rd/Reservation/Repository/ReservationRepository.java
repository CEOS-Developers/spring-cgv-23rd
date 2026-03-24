package com.ceos23.spring_cgv_23rd.Reservation.Repository;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
