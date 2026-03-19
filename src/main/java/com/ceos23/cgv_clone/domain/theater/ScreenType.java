package com.ceos23.cgv_clone.domain.theater;

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
    @Column(nullable = false)
    private ScreenTypeCode screenTypeCode;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private char maxRow;

    @Column(nullable = false)
    private int maxCol;

    @Builder
    public ScreenType(ScreenTypeCode screenTypeCode, int price, char maxRow, int maxCol) {
        this.screenTypeCode = screenTypeCode;
        this.price = price;
        this.maxRow = maxRow;
        this.maxCol = maxCol;
    }
}
