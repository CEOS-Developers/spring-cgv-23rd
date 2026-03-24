package com.ceos.spring_boot.domain.store.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {
    FOOD("Food"),
    DRINK("Drink"),
    GOODS("Goods");

    private final String description;
}
