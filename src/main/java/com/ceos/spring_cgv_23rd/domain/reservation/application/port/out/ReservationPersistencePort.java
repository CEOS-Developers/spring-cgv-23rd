package com.ceos.spring_cgv_23rd.domain.reservation.application.port.out;

import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.ReservationStatus;

import java.util.List;
import java.util.Optional;

public interface ReservationPersistencePort {
    
    Reservation saveReservation(Reservation reservation);

    Optional<Reservation> findReservationWithSeatsById(Long reservationId);

    void updateReservationStatus(Long reservationId, ReservationStatus status);

    List<Long> findReservedSeatIdsByScreeningId(Long screeningId);
}
