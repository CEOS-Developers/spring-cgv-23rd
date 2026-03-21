package com.ceos23.spring_boot.cgv.domain.cinema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer rowsCount;

    @Column(nullable = false)
    private Integer colsCount;

    public SeatLayout(String name, Integer rowsCount, Integer colsCount) {
        this.name = name;
        this.rowsCount = rowsCount;
        this.colsCount = colsCount;
    }
}