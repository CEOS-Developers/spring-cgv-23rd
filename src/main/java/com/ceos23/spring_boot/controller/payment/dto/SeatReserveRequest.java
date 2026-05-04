package com.ceos23.spring_boot.controller.payment.dto;

import com.ceos23.spring_boot.domain.reservation.dto.ReservationCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "좌석 선점 요청 DTO")
public record SeatReserveRequest(
        @Schema(description = "상영 일정 ID", example = "1")
        @NotNull(message = "상영 일정 ID는 필수입니다.")
        Long scheduleId,

        @Schema(description = "예매할 좌석 ID 목록", example = "[1, 2, 3]")
        @NotEmpty(message = "최소 1개 이상의 좌석을 선택해야 합니다.")
        List<Long> seatIds
) {
    public ReservationCreateCommand toCommand(String email) {
        return new ReservationCreateCommand(email, scheduleId, seatIds);
    }
}
