package com.ceos23.spring_boot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
        validateCount(count);

        this.itemOrder = itemOrder;
        this.item = item;
        this.count = count;
    }

    public static OrderDetail of(ItemOrder itemOrder, Item item, Integer count) {
        return new OrderDetail(itemOrder, item, count);
    }

    private void validateCount(Integer count) {
        if (count == null || count <= 0) {
            throw new IllegalArgumentException("상품 수량은 1 이상이어야 합니다.");
        }
    }
}