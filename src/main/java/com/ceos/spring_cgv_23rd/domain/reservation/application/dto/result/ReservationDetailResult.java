package com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PaymentResult;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
        LocalDateTime createdAt,
        PaymentResult payment
) {
    public static ReservationDetailResult of(Reservation reservation, ScreeningInfoResult screeningInfo, Map<Long, SeatInfoResult> seatInfoMap, PaymentResult payment) {
        List<SeatInfoResult> seats = reservation.getSeatIds().stream()
                .map(seatInfoMap::get)
                .toList();

        return new ReservationDetailResult(
                reservation.getId(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                screeningInfo.movieTitle(),
                screeningInfo.theaterName(),
                screeningInfo.hallName(),
                screeningInfo.startAt(),
                screeningInfo.endAt(),
                seats,
                reservation.getTotalPrice(),
                reservation.getCreatedAt(),
                payment
        );
    }
}
