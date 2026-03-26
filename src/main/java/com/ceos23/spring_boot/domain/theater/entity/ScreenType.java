package com.ceos23.spring_boot.domain.theater.entity;

import com.ceos23.spring_boot.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScreenType extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_type_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "surcharge_price", nullable = false)
    private Integer surchargePrice;

    @Builder
    public ScreenType(String name, Integer surchargePrice) {
        this.name = name;
        this.surchargePrice = surchargePrice;
    }
}