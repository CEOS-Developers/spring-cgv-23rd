package com.ceos.spring_cgv_23rd.domain.reservation.repository;

import com.ceos.spring_cgv_23rd.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.screening " +
            "WHERE r.id = :reservationId")
    Optional<Reservation> findWithScreeningById(Long reservationId);

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.screening s " +
            "LEFT JOIN FETCH r.guestEntity g " +
            "WHERE r.reservationNumber = :reservationNumber")
    Optional<Reservation> findWithScreeningByReservationNumber(String reservationNumber);
}
