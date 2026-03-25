package com.ceos23.cgv.domain.concession.entity;

import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "inventories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Inventory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private long id;

    @Min(value = 1, message = "재고는 최소 1개 이상입니다.")
    @Column(nullable = false)
    private int stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 재고 차감 로직
    public void removeStock(int quantity) {
        if (this.stockQuantity < quantity) {
            // 빼려는 수량보다 남은 재고가 적으면 에러 발생
            throw new CustomException(ErrorCode.INVENTORY_SHORTAGE);
        }
        this.stockQuantity -= quantity;
    }

    // 재고 수정 메서드
    public void updateStock(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
