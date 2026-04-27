package com.ceos23.cgv.domain.payment.dto;

public record PaymentResponse(
        String paymentId,
        String paymentStatus,
        String orderName,
        String pgProvider,
        String currency,
        String customData,
        String paidAt
) {
}
