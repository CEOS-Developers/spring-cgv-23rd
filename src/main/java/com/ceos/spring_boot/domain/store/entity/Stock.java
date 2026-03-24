package com.ceos.spring_boot.domain.store.entity;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "stocks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

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
    private Integer stock; // 현재 재고 수량
}
