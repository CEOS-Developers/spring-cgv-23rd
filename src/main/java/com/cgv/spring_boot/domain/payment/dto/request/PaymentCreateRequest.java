package com.cgv.spring_boot.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record PaymentCreateRequest(
        @NotBlank(message = "주문명은 필수입니다.")
        String orderName,

        @NotNull(message = "결제 금액은 필수입니다.")
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        Integer totalPayAmount,

        @NotBlank(message = "통화는 필수입니다.")
        @Pattern(regexp = "KRW|USD", message = "통화는 KRW 또는 USD만 가능합니다.")
        String currency,

        String customData
) {
}
