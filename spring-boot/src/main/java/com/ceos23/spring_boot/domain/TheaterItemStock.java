package com.ceos23.spring_boot.domain;

import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
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
public class TheaterItemStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer stock;

    private TheaterItemStock(Theater theater, Item item, Integer stock) {
        this.theater = theater;
        this.item = item;
        this.stock = stock;
    }

    public static TheaterItemStock of(Theater theater, Item item, Integer stock) {
        return new TheaterItemStock(theater, item, stock);
    }

    // 재고 검증 책임을 재고 도메인으로 이동
    public void ensureEnough(Integer quantity) {
        validateQuantity(quantity);

        if (this.stock < quantity) {
            throw new CustomException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }

    public void decreaseStock(Integer quantity) {
        ensureEnough(quantity);
        this.stock -= quantity;
    }

    public void increaseStock(Integer quantity) {
        validateQuantity(quantity);
        this.stock += quantity;
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
    }
}