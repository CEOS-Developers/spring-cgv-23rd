package com.cgv.spring_boot.domain.payment.entity;

import com.cgv.spring_boot.domain.payment.exception.PaymentErrorCode;
import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.global.common.entity.BaseEntity;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_pk")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(nullable = false, unique = true, length = 50)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private int totalAmount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column
    private String pgProvider;

    @Column(columnDefinition = "TEXT")
    private String customData;

    @Column
    private LocalDateTime paidAt;

    @Builder
    private Payment(
            Reservation reservation,
            String paymentId,
            PaymentStatus status,
            String orderName,
            int totalAmount,
            String currency,
            String pgProvider,
            String customData,
            LocalDateTime paidAt
    ) {
        this.reservation = reservation;
        this.paymentId = paymentId;
        this.status = status;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.pgProvider = pgProvider;
        this.customData = customData;
        this.paidAt = paidAt;
    }

    public static Payment createReady(
            Reservation reservation,
            String paymentId,
            String orderName,
            int totalAmount,
            String currency,
            String customData
    ) {
        return Payment.builder()
                .reservation(reservation)
                .paymentId(paymentId)
                .status(PaymentStatus.READY)
                .orderName(orderName)
                .totalAmount(totalAmount)
                .currency(currency)
                .customData(customData)
                .build();
    }

    public void markPaid(String pgProvider, LocalDateTime paidAt) {
        if (status != PaymentStatus.READY) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
        this.status = PaymentStatus.PAID;
        this.pgProvider = pgProvider;
        this.paidAt = paidAt;
    }

    public void markFailed() {
        if (status != PaymentStatus.READY) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
        this.status = PaymentStatus.FAILED;
    }

    public void cancel() {
        if (status != PaymentStatus.PAID) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_CANCELLABLE);
        }
        this.status = PaymentStatus.CANCELLED;
    }
}
