package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.Item;
import io.swagger.v3.oas.annotations.media.Schema;

public record ItemRequest(

        @Schema(description = "아이템 이름", example = "콜라")
        String name,

        @Schema(description = "아이템 가격", example = "2000")
        int price
) {
    public Item toEntity() {
        return Item.of(name, price);
    }
}