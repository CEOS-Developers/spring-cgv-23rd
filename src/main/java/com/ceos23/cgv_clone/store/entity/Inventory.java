package com.ceos23.cgv_clone.store.entity;

import com.ceos23.cgv_clone.global.entity.BaseEntity;
import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @Column(nullable = false)
    private int quantity; // 항상 1 이상일 것

    @Version
    private Long version;

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

    public void decrease(int quantity) {
        if (this.quantity < quantity) {
            throw new CustomException(ErrorCode.INSUFFICIENT_STOCK);
        }

        this.quantity -= quantity;
    }

    public void increase(int quantity) {
        this.quantity += quantity;
    }
}
