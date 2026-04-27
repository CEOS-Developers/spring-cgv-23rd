package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.Reservation;
import com.ceos23.spring_boot.domain.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long reservationId,
        Long userId,
        Long screeningId,
        Long seatId,
        ReservationStatus reservationStatus,
        String paymentId,
        LocalDateTime reservedAt,
        LocalDateTime paidAt,
        LocalDateTime expiresAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getScreening().getId(),
                reservation.getSeat().getId(),
                reservation.getReservationStatus(),
                reservation.getPaymentId(),
                reservation.getReservedAt(),
                reservation.getPaidAt(),
                reservation.getExpiresAt()
        );
    }
}