package com.ceos23.spring_boot.domain.store.entity;

import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.global.common.BaseSoftDeleteEntity;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UQ_INVENTORY_MENU", columnNames = {"menu_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Min(value = 1, message = "재고는 항상 1 이상이어야 합니다.")
    @Column(nullable = false)
    private Integer stock;

    @Builder
    public Inventory(Theater theater, Menu menu, Integer stock) {
        this.theater = theater;
        this.menu = menu;
        this.stock = stock;
    }

    public void decreaseStock(int quantity) {
        if (this.stock - quantity < 1) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }
        this.stock -= quantity;
    }
}