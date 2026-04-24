package com.ceos23.cgv_clone.store.entity;

import com.ceos23.cgv_clone.global.entity.BaseEntity;
import com.ceos23.cgv_clone.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private int totalPrice;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "store_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(String paymentId, OrderStatus orderStatus, int totalPrice, User user, Store store) {
        this.paymentId = paymentId;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.user = user;
        this.store = store;
    }

    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
    }

    public void markPaid() {
        this.orderStatus = OrderStatus.PAID;
    }

    public void markCanceled() {
        this.orderStatus = OrderStatus.CANCELED;
    }

    public void markFailed() {
        this.orderStatus = OrderStatus.FAILED;
    }
}
