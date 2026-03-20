package com.ceos.spring_boot.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ReservationRequest(
        @Schema(description = "상영 일정 ID", example = "1")
        Long scheduleId,

        @Schema(description = "예약할 좌석 목록")
        List<SeatRequest> seats
) {
    public record SeatRequest(
            @Schema(description = "행", example = "A")
            String row,

            @Schema(description = "열", example = "2")
            Integer column
    ) {
    }
}