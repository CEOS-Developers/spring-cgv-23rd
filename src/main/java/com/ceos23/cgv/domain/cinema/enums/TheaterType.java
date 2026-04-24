package com.ceos23.cgv.domain.cinema.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TheaterType {
    NORMAL(15000, "일반관"),
    IMAX(20000, "IMAX관"),
    FOUR_DX(20000, "4DX관"),
    SCREEN_X(20000, "SCREENX관"),
    SUITE(20000, "스위트관");

    private final int basePrice;
    private final String description;
}
