package com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationDetailResult(
        Long reservationId,
        String reservationNumber,
        ReservationStatus status,
        String movieTitle,
        String theaterName,
        String hallName,
        LocalDateTime startAt,
        LocalDateTime endAt,
        List<SeatInfoResult> seats,
        Integer totalPrice,
        LocalDateTime createdAt
) {
}
