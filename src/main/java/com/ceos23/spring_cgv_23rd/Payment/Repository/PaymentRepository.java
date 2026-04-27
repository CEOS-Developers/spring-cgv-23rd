package com.ceos23.spring_cgv_23rd.Payment.Repository;

import com.ceos23.spring_cgv_23rd.Payment.Domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTargetId(long targetId);
}
