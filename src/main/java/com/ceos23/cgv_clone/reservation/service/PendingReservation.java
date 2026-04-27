package com.ceos23.cgv_clone.reservation.service;

public record PendingReservation(
        Long reservationId,
        String paymentId,
        String orderName,
        int totalPrice
) {}
