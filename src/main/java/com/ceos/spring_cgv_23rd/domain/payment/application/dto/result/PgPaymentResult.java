package com.ceos.spring_cgv_23rd.domain.payment.application.dto.result;

import java.time.LocalDateTime;

public record PgPaymentResult(
        boolean paid,
        String pgProvider,
        LocalDateTime paidAt,
        String failureReason
) {

    public static PgPaymentResult success(String pgProvider, LocalDateTime paidAt) {
        return new PgPaymentResult(true, pgProvider, paidAt, null);
    }

    public static PgPaymentResult fail(String reason) {
        return new PgPaymentResult(false, null, null, reason);
    }
}
