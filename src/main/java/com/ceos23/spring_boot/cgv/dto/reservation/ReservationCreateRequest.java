package com.ceos23.spring_boot.cgv.dto.reservation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReservationCreateRequest(
        @NotNull Long userId,
        @NotNull Long screeningId,
        @NotEmpty List<Long> seatTemplateIds
) {
}
