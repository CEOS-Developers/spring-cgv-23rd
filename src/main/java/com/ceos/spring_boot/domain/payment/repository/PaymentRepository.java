package com.ceos.spring_boot.domain.payment.repository;

import com.ceos.spring_boot.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);

    long countByPaymentIdStartingWith(String prefix);
}
