package com.ceos23.cgv.domain.concession.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {
    POPCORN("팝콘"),
    DRINK("음료"),
    SNACK("스낵"),
    COMBO("콤보");

    private final String description;
}
