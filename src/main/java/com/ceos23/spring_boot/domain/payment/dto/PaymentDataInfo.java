package com.ceos23.spring_boot.domain.payment.dto;

import com.ceos23.spring_boot.domain.payment.client.dto.PaymentData;

public record PaymentDataInfo(
        String paymentId,
        String paymentStatus,
        String orderName,
        String pgProvider,
        String currency,
        String customData,
        String paidAt
) {
    public static PaymentDataInfo from(PaymentData data) {
        return new PaymentDataInfo(
                data.paymentId(),
                data.paymentStatus(),
                data.orderName(),
                data.pgProvider(),
                data.currency(),
                data.customData(),
                data.paidAt()
        );
    }
}