package com.ceos23.spring_boot.cgv.dto.payment;

import com.ceos23.spring_boot.cgv.domain.payment.PaymentLog;
import java.time.LocalDateTime;

public record PaymentResponse(
        String paymentId,
        String paymentStatus,
        String orderName,
        Long amount,
        String detail,
        LocalDateTime requestedAt
) {
    public static PaymentResponse from(PaymentLog paymentLog) {
        return new PaymentResponse(
                paymentLog.getPaymentId(),
                paymentLog.getStatus().name(),
                paymentLog.getOrderName(),
                paymentLog.getAmount(),
                paymentLog.getDetail(),
                paymentLog.getCreatedAt()
        );
    }
}
