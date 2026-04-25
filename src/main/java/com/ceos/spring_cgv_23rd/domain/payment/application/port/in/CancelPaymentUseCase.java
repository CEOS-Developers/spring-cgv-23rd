package com.ceos.spring_cgv_23rd.domain.payment.application.port.in;

public interface CancelPaymentUseCase {

    void cancel(String paymentId);
}
