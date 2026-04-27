package com.ceos.spring_boot.domain.payment.dto;

import com.ceos.spring_boot.domain.payment.entity.PaymentCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record PaymentRequest(

        @Schema(description = "가맹점 ID (GitHub ID)", example = "Seungwon326")
        String storeId,

        @Schema(description = "주문명", example = "왕과 사는 남자 예매")
        String orderName,

        @Schema(description = "결제 금액", example = "15000")
        Integer totalPayAmount,

        @Schema(description = "결제 카테고리 (MOVIE | STORE)", example = "MOVIE")
        PaymentCategory category,

        @Schema(description = "예매 ID 또는 주문 ID", example = "1")
        Long targetId,

        @Schema(description = "통화 (KRW | USD)", example = "KRW")
        String currency,

        @Schema(description = "가맹점 커스텀 데이터", example = "{\"reservationId\": 1}")
        Map<String, Object> customData
) {

    public static PaymentRequest of(String storeId, String orderName, Integer amount,
                                    PaymentCategory category, Long targetId, Map<String, Object> customData) {
        return new PaymentRequest(
                storeId, orderName, amount, category,
                targetId, "KRW", customData
        );
    }
}
