package com.ceos23.spring_boot.controller.reservation.dto;

import com.ceos23.spring_boot.domain.reservation.dto.ReservationInfo;
import com.ceos23.spring_boot.domain.reservation.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "예매 상세 응답 데이터")
public record ReservationResponse(
        @Schema(description = "회원 고유 ID", example = "1")
        Long userId,

        @Schema(description = "예매 고유 ID", example = "1")
        Long reservationId,

        @Schema(description = "예매한 상영 일정 ID", example = "1")
        Long scheduleId,

        @Schema(description = "예매 상태", example = "RESERVED")
        ReservationStatus status,

        @Schema(description = "예매된 좌석 ID 목록", example = "[1, 2]")
        List<Long> reservedSeatIds
) {
    public static ReservationResponse from(ReservationInfo info) {
        return new ReservationResponse(
                info.userId(),
                info.reservationId(),
                info.scheduleId(),
                info.status(),
                info.reservedSeatIds()
        );
    }
}
