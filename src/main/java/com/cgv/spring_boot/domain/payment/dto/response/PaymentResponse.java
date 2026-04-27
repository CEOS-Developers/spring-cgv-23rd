package com.cgv.spring_boot.domain.payment.dto.response;

import java.time.LocalDateTime;

public record PaymentResponse(
        String paymentId,
        String paymentStatus,
        String orderName,
        String pgProvider,
        String currency,
        String customData,
        LocalDateTime paidAt
) {
}
