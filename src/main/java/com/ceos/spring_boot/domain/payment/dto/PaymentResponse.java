package com.ceos.spring_boot.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PaymentResponse(

        @Schema(description = "가맹점이 생성한 고유 결제 ID", example = "20260402_SEUNGWON326_001")
        String paymentId,

        @Schema(description = "결제 상태 (PAID | FAILED | CANCELLED)", example = "PAID")
        String paymentStatus,

        @Schema(description = "주문명", example = "왕과 사는 남자 예매")
        String orderName,

        @Schema(description = "결제 대행사 정보", example = "KAKAOPAY")
        String pgProvider,

        @Schema(description = "통화 단위", example = "KRW")
        String currency,

        @Schema(description = "가맹점 커스텀 데이터 (우리 서버의 식별자 등)", example = "{\"reservationId\": 1}")
        String customData,

        @Schema(description = "결제/취소 완료 시간", example = "2026-04-02T13:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime paidAt
) {}
