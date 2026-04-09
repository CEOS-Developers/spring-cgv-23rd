package com.ceos23.spring_boot.domain.payment.client.dto;

public record PaymentData(
        String paymentId,
        String paymentStatus,
        String orderName,
        String pgProvider,
        String currency,
        String customData,
        String paidAt
) {}
