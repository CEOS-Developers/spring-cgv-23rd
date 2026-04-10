package com.ceos.spring_cgv_23rd.domain.product.adapter.out.persistence.entity;

import com.ceos.spring_cgv_23rd.domain.product.domain.OrderStatus;
import com.ceos.spring_cgv_23rd.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_order")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_order_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "theater_id", nullable = false)
    private Long theaterId;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems = new ArrayList<>();


    public void addOrderItem(OrderItemEntity orderItemEntity) {
        this.orderItems.add(orderItemEntity);
        orderItemEntity.assignOrder(this);
    }

    public void updateStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }
}
