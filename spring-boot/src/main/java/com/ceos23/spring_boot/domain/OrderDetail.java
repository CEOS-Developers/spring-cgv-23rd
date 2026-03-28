package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_order_id", nullable = false)
    private ItemOrder itemOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer count;

    private OrderDetail(ItemOrder itemOrder, Item item, Integer count) {
        this.itemOrder = itemOrder;
        this.item = item;
        this.count = count;
    }

    public static OrderDetail of(ItemOrder itemOrder, Item item, Integer count) {
        return new OrderDetail(itemOrder, item, count);
    }
}