package com.ceos.spring_cgv_23rd.domain.product.entity;

import com.ceos.spring_cgv_23rd.domain.product.enums.OrderStatus;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.TheaterEntity;
import com.ceos.spring_cgv_23rd.domain.user.adapter.out.persistence.entity.UserEntity;
import com.ceos.spring_cgv_23rd.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private TheaterEntity theater;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();


    public static ProductOrder createOrder(UserEntity userEntity, TheaterEntity theater, List<OrderItem> orderItems) {

        int totalPrice = orderItems.stream()
                .mapToInt(item -> item.getQuantity() * item.getPrice())
                .sum();

        ProductOrder order = ProductOrder.builder()
                .userEntity(userEntity)
                .theater(theater)
                .orderNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .totalPrice(totalPrice)
                .status(OrderStatus.COMPLETED)
                .build();

        orderItems.forEach(order::addOrderItem);

        return order;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.assignOrder(this);
    }
}
