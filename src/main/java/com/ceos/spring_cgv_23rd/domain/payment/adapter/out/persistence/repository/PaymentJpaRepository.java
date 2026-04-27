package com.ceos.spring_cgv_23rd.domain.payment.adapter.out.persistence.repository;

import com.ceos.spring_cgv_23rd.domain.payment.adapter.out.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByPaymentId(String paymentId);
}
