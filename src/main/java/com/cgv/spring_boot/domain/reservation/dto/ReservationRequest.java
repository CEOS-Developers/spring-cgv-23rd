package com.cgv.spring_boot.domain.reservation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ReservationRequest(
        @NotNull(message = "스케줄 ID는 필수입니다.")
        Long scheduleId,

        @Valid
        @NotEmpty(message = "좌석은 1개 이상 선택해야 합니다.")
        List<SeatRequest> seats
) {
    public record SeatRequest(
            @NotBlank(message = "좌석 행은 필수입니다.")
            String seatRow,

            @Positive(message = "좌석 열은 1 이상이어야 합니다.")
            int seatCol
    ) {
    }
}
