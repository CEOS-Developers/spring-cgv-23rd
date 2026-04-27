package com.ceos.spring_cgv_23rd.domain.payment.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.payment.domain.Payment;
import com.ceos.spring_cgv_23rd.domain.payment.domain.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResult(
        String paymentId,
        PaymentStatus status,
        Integer amount,
        String orderName,
        String pgProvider,
        LocalDateTime paidAt
) {
    public static PaymentResult of(Payment payment) {
        return new PaymentResult(
                payment.getPaymentId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getOrderName(),
                payment.getPgProvider(),
                payment.getPaidAt());
    }
}
