package com.ceos.spring_boot.domain.payment.entity;

import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.entity.BaseEntity;
import com.ceos.spring_boot.global.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_payment_id", columnNames = {"payment_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_id", nullable = false, unique = true)
    private String paymentId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    // 어떤 도메인(영화/매점)에서 왔는지 구분
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PaymentCategory category; // MOVIE, STORE

    @Column(name = "paid_at", nullable = true, updatable = false)
    protected LocalDateTime paidAt;

    public static Payment createPayment(String paymentId, Integer amount, Long targetId, PaymentCategory category) {
        return Payment.builder()
                .paymentId(paymentId)
                .amount(amount)
                .status(PaymentStatus.READY)
                .targetId(targetId)
                .category(category)
                .build();
    }


    public void markAsPaid() {
        if (this.status != PaymentStatus.READY) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void cancel() {
        if (this.status == PaymentStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELLED_PAYMENT);
        }
        if (this.status != PaymentStatus.PAID) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_CANCELLABLE);
        }
        this.status = PaymentStatus.CANCELLED;
    }

    @Builder
    public Payment(String paymentId, Integer amount, PaymentStatus status, Long targetId, PaymentCategory category) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.targetId = targetId;
        this.category = category;
    }
}
