package com.ceos23.cgv_clone.domain.theater;

import com.ceos23.cgv_clone.domain.enums.ScreenTypeCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "screen_types")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ScreenType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_type_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "screen_type_name")
    private ScreenTypeCode screenTypeCode;

    @Column(nullable = false, name = "screen_type_price")
    private double price;

    @Column(nullable = false, name = "screen_type_max_row")
    private char rowName;

    @Column(nullable = false, name = "screen_type_max_col")
    private int colName;

    @Builder
    public ScreenType(ScreenTypeCode screenTypeCode, double price, char rowName, int colName) {
        this.screenTypeCode = screenTypeCode;
        this.price = price;
        this.rowName = rowName;
        this.colName = colName;
    }
}
