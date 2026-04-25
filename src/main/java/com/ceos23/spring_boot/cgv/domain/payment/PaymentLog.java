package com.ceos23.spring_boot.cgv.domain.payment;

import com.ceos23.spring_boot.cgv.domain.BaseEntity;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "payment_log",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_id", columnNames = "payment_id")
        }
)
public class PaymentLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false, updatable = false, length = 100)
    private String paymentId;

    @Column(nullable = false, length = 200)
    private String orderName;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 1000)
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    public PaymentLog(String paymentId, String orderName, Long amount, String detail) {
        this.paymentId = paymentId;
        this.orderName = orderName;
        this.amount = amount;
        this.detail = detail;
        this.status = PaymentStatus.PAID;
    }

    public void cancel() {
        if (status != PaymentStatus.PAID) {
            throw new ConflictException(ErrorCode.PAYMENT_NOT_CANCELLABLE);
        }

        this.status = PaymentStatus.CANCELLED;
    }
}
