package com.ceos.spring_cgv_23rd.domain.payment.adapter.out.pg.dto;

public record PgPaymentRequest(
        String storeId,
        String orderName,
        Integer totalPayAmount,
        String currency,
        String customData
) {
}