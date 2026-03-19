package com.ceos23.spring_boot.domain.theater.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatGrade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_grade_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "surcharge_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal surchargePrice;
}
