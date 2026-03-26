package com.ceos23.cgv.domain.cinema.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TheaterType {
    NORMAL(15000),       // 일반관 15,000원
    IMAX(20000),         // 특별관 20,000원
    FOUR_DX(20000),
    SCREEN_X(20000),
    SUITE(20000);

    private final int basePrice;
}
