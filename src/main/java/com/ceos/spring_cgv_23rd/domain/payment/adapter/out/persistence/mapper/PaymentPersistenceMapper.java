package com.ceos.spring_cgv_23rd.domain.payment.adapter.out.persistence.mapper;

import com.ceos.spring_cgv_23rd.domain.payment.adapter.out.persistence.entity.PaymentEntity;
import com.ceos.spring_cgv_23rd.domain.payment.domain.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentPersistenceMapper {

    //  Entity → Domain
    public Payment toDomain(PaymentEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .paymentId(entity.getPaymentId())
                .status(entity.getStatus())
                .orderName(entity.getOrderName())
                .amount(entity.getAmount())
                .pgProvider(entity.getPgProvider())
                .paidAt(entity.getPaidAt())
                .cancelledAt(entity.getCancelledAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    //  Domain → Entity
    public PaymentEntity toEntity(Payment domain) {
        return PaymentEntity.createPaymentEntity(
                domain.getPaymentId(),
                domain.getStatus(),
                domain.getOrderName(),
                domain.getAmount());
    }
}
