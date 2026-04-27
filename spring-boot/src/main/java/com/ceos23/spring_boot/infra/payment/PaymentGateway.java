package com.ceos23.spring_boot.infra.payment;

import com.ceos23.spring_boot.infra.payment.dto.PaymentData;

public interface PaymentGateway {

    PaymentData pay(String paymentId, String orderName, Integer totalPayAmount, String customData);

    PaymentData cancel(String paymentId);

    PaymentData getPayment(String paymentId);
}