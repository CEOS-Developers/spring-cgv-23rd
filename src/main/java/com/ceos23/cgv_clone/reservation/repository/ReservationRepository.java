package com.ceos23.cgv_clone.reservation.repository;

import com.ceos23.cgv_clone.reservation.entity.Reservation;
import com.ceos23.cgv_clone.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByStatusAndReservedAtBefore(ReservationStatus status, LocalDateTime threshold);
}
