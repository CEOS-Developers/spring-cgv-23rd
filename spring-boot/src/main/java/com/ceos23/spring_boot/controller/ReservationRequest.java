package com.ceos23.spring_boot.controller;

public record ReservationRequest(
        Long userId,
        Long screeningId,
        Long seatId
) {
}