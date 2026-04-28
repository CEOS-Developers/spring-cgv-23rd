package com.ceos23.spring_boot.domain.reservation.dto;

import com.ceos23.spring_boot.domain.reservation.entity.Reservation;
import com.ceos23.spring_boot.domain.reservation.entity.ReservationStatus;
import com.ceos23.spring_boot.domain.reservation.entity.ReservedSeat;

import java.util.List;

public record ReservationInfo(
        Long userId,
        Long reservationId,
        Long scheduleId,
        ReservationStatus status,
        Integer totalPrice,
        String paymentId,
        List<Long> reservedSeatIds,
        String orderName
) {
    public static ReservationInfo from(Reservation reservation, List<ReservedSeat> reservedSeats, String orderName) {
        List<Long> seatIds = reservedSeats.stream()
                .map(rs -> rs.getSeat().getId())
                .toList();

        return new ReservationInfo(
                reservation.getUser().getId(),
                reservation.getId(),
                reservation.getSchedule().getId(),
                reservation.getStatus(),
                reservation.getTotalPrice(),
                reservation.getPaymentId(),
                seatIds,
                orderName
        );
    }
}
