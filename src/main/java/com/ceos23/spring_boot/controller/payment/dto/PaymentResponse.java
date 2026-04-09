package com.ceos23.spring_boot.controller.payment.dto;

import com.ceos23.spring_boot.domain.payment.client.dto.PaymentData;
import com.ceos23.spring_boot.domain.payment.dto.PaymentDataInfo;

public record PaymentResponse(
        String paymentId,
        String paymentStatus,
        String orderName,
        String pgProvider,
        String currency,
        String customData,
        String paidAt
) {
    public static PaymentResponse from(PaymentDataInfo info) {
        return new PaymentResponse(
                info.paymentId(),
                info.paymentStatus(),
                info.orderName(),
                info.pgProvider(),
                info.currency(),
                info.customData(),
                info.paidAt()
        );
    }
}
