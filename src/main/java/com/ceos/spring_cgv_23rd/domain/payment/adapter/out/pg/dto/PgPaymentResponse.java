package com.ceos.spring_cgv_23rd.domain.payment.adapter.out.pg.dto;

import java.time.LocalDateTime;

public record PgPaymentResponse(
        Integer status,
        String message,
        PgPaymentData payload
) {
    public record PgPaymentData(
            String paymentId,
            String paymentStatus,
            String orderName,
            String pgProvider,
            String currency,
            String customData,
            LocalDateTime paidAt
    ) {
    }
}
