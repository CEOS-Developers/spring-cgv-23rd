package com.ceos23.spring_boot.domain.reservation.repository;

import com.ceos23.spring_boot.domain.reservation.entity.Reservation;
import com.ceos23.spring_boot.domain.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByPaymentId(String orderNumber);

    List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime cutoffTime);
}
