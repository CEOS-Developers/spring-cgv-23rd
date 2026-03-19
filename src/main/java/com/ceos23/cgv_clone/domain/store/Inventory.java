package com.ceos23.cgv_clone.domain.store;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @Column(nullable = false)
    private int quantity; // 항상 1 이상일 것

    @JoinColumn(name = "store_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @JoinColumn(name = "menu_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Menu menu;

    @Builder
    public Inventory(int quantity, Store store, Menu menu) {
        this.quantity = quantity;
        this.store = store;
        this.menu = menu;
    }
}
