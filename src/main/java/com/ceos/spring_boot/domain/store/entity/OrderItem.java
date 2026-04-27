package com.ceos.spring_boot.domain.store.entity;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer count; // 구매 수량

    public static OrderItem create(Order order, Product product, Integer count) {
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .count(count)
                .build();

        order.addOrderItem(orderItem);

        return orderItem;
    }

    public void setOrder(Order order) {
        this.order = order;

        if (!order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }

}
