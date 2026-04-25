package com.ceos23.spring_boot.cgv.repository.payment;

import com.ceos23.spring_boot.cgv.domain.payment.PaymentLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {

    Optional<PaymentLog> findByPaymentId(String paymentId);
}
