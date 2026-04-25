package com.ceos23.spring_boot.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private String paymentId;

    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    @OneToMany(mappedBy = "itemOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    private ItemOrder(User user, Theater theater, Integer totalPrice) {
        this.user = user;
        this.theater = theater;
        this.totalPrice = totalPrice;
        this.orderStatus = OrderStatus.PENDING_PAYMENT;
    }

    public static ItemOrder of(User user, Theater theater, Integer totalPrice) {
        return new ItemOrder(user, theater, totalPrice);
    }

    @PrePersist
    protected void onCreate() {
        this.orderedAt = LocalDateTime.now();
    }

    // 주문 상세 생성 책임을 주문 도메인으로 이동
    public void addOrderDetail(Item item, Integer count) {
        OrderDetail orderDetail = OrderDetail.of(this, item, count);
        this.orderDetails.add(orderDetail);
    }

    public void markPaid(String paymentId, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.paidAt = paidAt;
        this.orderStatus = OrderStatus.PAID;
    }

    public void markPaymentFailed(String paymentId) {
        this.paymentId = paymentId;
        this.orderStatus = OrderStatus.PAYMENT_FAILED;
    }

    public void cancel(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
        this.orderStatus = OrderStatus.CANCELLED;
    }
}