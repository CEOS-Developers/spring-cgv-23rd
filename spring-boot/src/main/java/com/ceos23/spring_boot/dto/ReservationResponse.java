package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.Reservation;

public record ReservationResponse(
        Long reservationId,
        Long userId,
        Long screeningId,
        Long seatId
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getScreening().getId(),
                reservation.getSeat().getId()
        );
    }
}