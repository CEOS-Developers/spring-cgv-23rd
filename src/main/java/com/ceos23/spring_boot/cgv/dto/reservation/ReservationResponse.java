package com.ceos23.spring_boot.cgv.dto.reservation;

import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationSeat;
import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long reservationId,
        Long userId,
        Long screeningId,
        String paymentId,
        String status,
        LocalDateTime reservedAt,
        LocalDateTime expiresAt,
        List<Long> seatTemplateIds
) {
    public static ReservationResponse of(Reservation reservation, List<ReservationSeat> reservationSeats) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getScreening().getId(),
                reservation.getPaymentId(),
                reservation.getStatus().name(),
                reservation.getReservedAt(),
                reservation.getExpiresAt(),
                reservationSeats.stream()
                        .map(reservationSeat -> reservationSeat.getSeatTemplate().getId())
                        .toList()
        );
    }
}
