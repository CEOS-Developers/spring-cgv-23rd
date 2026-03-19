package com.ceos23.cgv_clone.domain.store;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int unitPrice; // 구매 시점에서의 가격 (가격 변동이 있어도, 과거의 가격이 변경되면 안된다)

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @JoinColumn(name = "inventory_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Inventory inventory;

    @Builder
    public OrderItem(int quantity, int unitPrice, Order order, Inventory inventory) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.order = order;
        this.inventory = inventory;
    }
}
