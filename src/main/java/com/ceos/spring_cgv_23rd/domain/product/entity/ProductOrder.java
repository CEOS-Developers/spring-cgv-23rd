package com.ceos.spring_cgv_23rd.domain.product.entity;

import com.ceos.spring_cgv_23rd.domain.product.enums.OrderStatus;
import com.ceos.spring_cgv_23rd.domain.theater.entity.Theater;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_order")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    public static ProductOrder createOrder(User user, Theater theater, int totalPrice) {
        return ProductOrder.builder()
                .user(user)
                .theater(theater)
                .orderNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .totalPrice(totalPrice)
                .status(OrderStatus.COMPLETED)
                .build();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
}
