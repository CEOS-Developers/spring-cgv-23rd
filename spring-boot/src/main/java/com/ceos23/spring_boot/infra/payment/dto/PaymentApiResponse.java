package com.ceos23.spring_boot.infra.payment.dto;

public record PaymentApiResponse<T>(
        Integer code,
        String message,
        T data
) {
}