package com.ceos.spring_cgv_23rd.domain.payment.application.port.out;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PgPaymentResult;
import com.ceos.spring_cgv_23rd.domain.payment.domain.Payment;

public interface PgClientPort {

    PgPaymentResult pay(Payment payment);

    void cancel(String paymentId);

}
