package com.ceos.spring_cgv_23rd.domain.payment.application.port.out;

import com.ceos.spring_cgv_23rd.domain.payment.domain.Payment;

import java.util.Optional;

public interface PaymentPersistencePort {

    Payment save(Payment payment);

    Optional<Payment> findByPaymentId(String paymentId);

    void update(Payment payment);
}
