package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.Item;

public record ItemRequest(
        String name,
        int price
) {
    public Item toEntity() {
        return new Item(null, name, price);
    }
}