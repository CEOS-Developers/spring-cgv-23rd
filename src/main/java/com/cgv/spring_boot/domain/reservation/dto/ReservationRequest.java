package com.cgv.spring_boot.domain.reservation.dto;

import java.util.List;

public record ReservationRequest(
        Long scheduleId,
        List<SeatRequest> seats
) {
    public record SeatRequest(String seatRow, int seatCol) {
    }
}
