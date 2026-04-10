package com.ceos23.spring_boot.dto;

public record ReservationRequest(
        Long userId,
        Long screeningId,
        Long seatId
) {
}