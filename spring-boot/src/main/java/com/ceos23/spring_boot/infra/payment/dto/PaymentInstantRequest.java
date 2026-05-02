package com.ceos23.spring_boot.infra.payment.dto;

public record PaymentInstantRequest(
        String storeId,
        String orderName,
        Integer totalPayAmount,
        String currency,
        String customData
) {
}