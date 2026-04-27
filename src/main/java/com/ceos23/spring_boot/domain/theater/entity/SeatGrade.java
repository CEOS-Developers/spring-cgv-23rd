package com.ceos23.spring_boot.domain.theater.entity;

import com.ceos23.spring_boot.global.common.BaseSoftDeleteEntity;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatGrade extends BaseSoftDeleteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_grade_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "surcharge_price", nullable = false)
    private Integer surchargePrice;

    @Builder
    public SeatGrade(String name, Integer surchargePrice) {
        this.name = name;
        this.surchargePrice = surchargePrice;
    }
}
