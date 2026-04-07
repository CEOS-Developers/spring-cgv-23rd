package com.ceos.spring_cgv_23rd.domain.product.entity;

import com.ceos.spring_cgv_23rd.domain.product.exception.ProductErrorCode;
import com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity.TheaterEntity;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"theater_id", "product_id"})
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private TheaterEntity theater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;


    public void decreaseQuantity(int count) {
        if (this.quantity < count) {
            throw new GeneralException(ProductErrorCode.INSUFFICIENT_STOCK);
        }

        this.quantity -= count;
    }

    public void increaseQuantity(int count) {
        this.quantity += count;
    }
}
