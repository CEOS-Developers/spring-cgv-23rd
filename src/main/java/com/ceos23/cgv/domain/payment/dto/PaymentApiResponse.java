package com.ceos23.cgv.domain.payment.dto;

public record PaymentApiResponse(
        int status,
        String message,
        PaymentResponse payload
) {
}
