package com.ceos23.spring_boot.cgv.service.payment;

public record PaymentCreateCommand(
        String paymentId,
        String orderName,
        Long amount,
        String detail
) {
}
