package com.ceos23.spring_boot.infra.payment.dto;

import java.time.LocalDateTime;

public record PaymentData(
        String paymentId,
        String paymentStatus,
        String orderName,
        String pgProvider,
        String currency,
        String customData,
        LocalDateTime paidAt
) {
}