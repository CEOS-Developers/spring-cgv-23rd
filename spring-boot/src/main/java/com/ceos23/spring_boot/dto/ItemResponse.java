package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.Item;

public record ItemResponse(
        Long id,
        String name,
        int price
) {
    // 정적 팩토리 메서드
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice()
        );
    }
}