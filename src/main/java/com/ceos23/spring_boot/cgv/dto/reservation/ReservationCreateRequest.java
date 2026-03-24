package com.ceos23.spring_boot.cgv.dto.reservation;

import java.util.List;

public record ReservationCreateRequest(
        Long userId,
        Long screeningId,
        List<Long> seatTemplateIds
) {
}