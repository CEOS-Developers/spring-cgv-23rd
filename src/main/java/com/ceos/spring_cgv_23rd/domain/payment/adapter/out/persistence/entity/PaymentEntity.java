package com.ceos.spring_cgv_23rd.domain.payment.adapter.out.persistence.entity;

import com.ceos.spring_cgv_23rd.domain.payment.domain.Payment;
import com.ceos.spring_cgv_23rd.domain.payment.domain.PaymentStatus;
import com.ceos.spring_cgv_23rd.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_id", nullable = false, unique = true)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "pg_provider")
    private String pgProvider;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;


    private PaymentEntity(String paymentId, PaymentStatus status, String orderName, Integer amount) {
        this.paymentId = paymentId;
        this.status = status;
        this.orderName = orderName;
        this.amount = amount;
    }

    public static PaymentEntity createPaymentEntity(String paymentId, PaymentStatus status, String orderName, Integer amount) {
        return new PaymentEntity(paymentId, status, orderName, amount);
    }

    public void updateFrom(Payment payment) {
        this.status = payment.getStatus();
        this.pgProvider = payment.getPgProvider();
        this.paidAt = payment.getPaidAt();
        this.cancelledAt = payment.getCancelledAt();
    }
}
