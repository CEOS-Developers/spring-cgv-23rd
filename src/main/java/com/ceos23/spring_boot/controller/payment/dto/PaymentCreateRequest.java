package com.ceos23.spring_boot.controller.payment.dto;

import com.ceos23.spring_boot.domain.payment.dto.FrontendPaymentRequest;
import com.ceos23.spring_boot.domain.reservation.dto.ReservationCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "결제 및 예매 요청 DTO")
public record PaymentCreateRequest(
        @Schema(description = "상영 일정 ID", example = "1")
        @NotNull(message = "상영 일정 ID는 필수입니다.")
        Long scheduleId,

        @Schema(description = "예매할 좌석 ID 목록", example = "[1, 2, 3]")
        @NotEmpty(message = "최소 1개 이상의 좌석을 선택해야 합니다.")
        List<Long> seatIds,

        @Schema(description = "사용자가 확인한 예상 결제 금액 (위변조 검증용)", example = "30000")
        @NotNull(message = "예상 결제 금액은 필수입니다.")
        Integer expectedAmount,

        @Schema(description = "결제 고유 ID (예매 번호)", example = "20240520_a1b2c3d4")
        @NotEmpty(message = "결제 ID는 필수입니다.")
        String paymentId
) {
    public ReservationCreateCommand toCommand(String email) {
        return new ReservationCreateCommand(email, scheduleId, seatIds);
    }

    public FrontendPaymentRequest toFrontendRequest() {
        return new FrontendPaymentRequest(expectedAmount);
    }
}