package com.ceos.spring_cgv_23rd.domain.reservation.application.dto.result;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResult(
        String reservationToken,
        Long screeningId,
        List<Long> seatIds,
        LocalDateTime expiresAt
) {
}
