package com.ceos23.cgv.domain.payment.dto;

public record PaymentInstantRequest(
        String storeId,
        String orderName,
        int totalPayAmount,
        String currency,
        String customData
) {
}
