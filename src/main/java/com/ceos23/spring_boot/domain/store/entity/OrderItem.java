package com.ceos23.spring_boot.domain.store.entity;

import com.ceos23.spring_boot.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_price", nullable = false)
    private Integer orderPrice;

    @Column(nullable = false)
    private Integer count;

    @Builder
    public OrderItem(Menu menu, Order order, Integer orderPrice, Integer count) {
        this.menu = menu;
        this.order = order;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    public void updateOrder(Order order) {
        this.order = order;
    }
}
