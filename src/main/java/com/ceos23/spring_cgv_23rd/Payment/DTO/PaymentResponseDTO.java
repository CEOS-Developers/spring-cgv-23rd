package com.ceos23.spring_cgv_23rd.Payment.DTO;

public record PaymentResponseDTO(
        Integer code,
        String message,
        PaymentResultDTO payload
) {
    public boolean findStatus(String status){
        return payload.paymentStatus().equals(status);
    }

}