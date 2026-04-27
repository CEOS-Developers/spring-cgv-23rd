package com.ceos23.spring_boot.domain.reservation.dto;

import java.util.List;

public record ReservationCreateCommand(
        Long userId,
        Long scheduleId,
        List<Long> seatIds
) {
}
