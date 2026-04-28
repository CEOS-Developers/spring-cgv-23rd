package com.ceos23.spring_boot.domain.payment.client.dto;

import com.ceos23.spring_boot.domain.payment.dto.FrontendPaymentRequest;

public record PaymentRequest (
        String storeId,
        String orderName,
        Integer totalPayAmount,
        String currency,
        String customData
){
}
