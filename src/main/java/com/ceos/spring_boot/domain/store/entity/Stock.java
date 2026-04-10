package com.ceos.spring_boot.domain.store.entity;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "stocks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema; // 어느 지점인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 어떤 상품인지

    @Column(nullable = false)
    private Integer quantity; // 현재 재고 수량

    public void decreaseQuantity(Integer amount) {
        int restStock = this.quantity - amount;
        if (restStock < 0) {
            throw new IllegalStateException(ErrorCode.OUT_OF_STOCK_ERROR.getMessage());
        }
        this.quantity = restStock;
    }

}
