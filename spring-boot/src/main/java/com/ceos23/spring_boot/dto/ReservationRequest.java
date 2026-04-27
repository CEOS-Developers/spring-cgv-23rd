package com.ceos23.spring_boot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationRequest(
        @NotNull(message = "상영 ID는 필수입니다.")
        @Positive(message = "상영 ID는 양수여야 합니다.")
        Long screeningId,

        @NotNull(message = "좌석 ID는 필수입니다.")
        @Positive(message = "좌석 ID는 양수여야 합니다.")
        Long seatId
) {
}