package com.ceos23.cgv_clone.reservation.service.dto;

public record PendingReservation(
        Long reservationId,
        String paymentId,
        String orderName,
        int totalPrice
) {}
