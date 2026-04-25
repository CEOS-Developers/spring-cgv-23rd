package com.ceos.spring_cgv_23rd.domain.payment.adapter.out.persistence;

import com.ceos.spring_cgv_23rd.domain.payment.adapter.out.persistence.entity.PaymentEntity;
import com.ceos.spring_cgv_23rd.domain.payment.adapter.out.persistence.mapper.PaymentPersistenceMapper;
import com.ceos.spring_cgv_23rd.domain.payment.adapter.out.persistence.repository.PaymentJpaRepository;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.out.PaymentPersistencePort;
import com.ceos.spring_cgv_23rd.domain.payment.domain.Payment;
import com.ceos.spring_cgv_23rd.domain.payment.exception.PaymentErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentPersistencePort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentPersistenceMapper mapper;

    @Override
    public Payment save(Payment payment) {
        return mapper.toDomain(paymentJpaRepository.save(mapper.toEntity(payment)));
    }

    @Override
    public Optional<Payment> findByPaymentId(String paymentId) {
        return paymentJpaRepository.findByPaymentId(paymentId)
                .map(mapper::toDomain);
    }

    @Override
    public void update(Payment payment) {
        PaymentEntity paymentEntity = paymentJpaRepository.findByPaymentId(payment.getPaymentId())
                .orElseThrow(() -> new GeneralException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        paymentEntity.updateFrom(payment);
    }
}
