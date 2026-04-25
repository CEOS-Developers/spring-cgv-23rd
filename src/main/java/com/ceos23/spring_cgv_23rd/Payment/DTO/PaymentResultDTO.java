package com.ceos23.spring_cgv_23rd.Payment.DTO;

public record PaymentResultDTO(
        String paymentId,
        String paymentStatus,
        String orderName,
        String pgProvider,
        String currency,
        String customData,
        String paidAt
){

}