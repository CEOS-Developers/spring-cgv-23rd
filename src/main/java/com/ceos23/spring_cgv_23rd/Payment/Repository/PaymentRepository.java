package com.ceos23.spring_cgv_23rd.Payment.Repository;

import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTargetId(long targetId);

    Optional<Payment> findByPaymentId(String paymentId);
}
