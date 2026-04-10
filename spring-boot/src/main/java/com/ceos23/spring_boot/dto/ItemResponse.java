package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ItemResponse {

    @Schema(description = "아이템 이름", example = "콜라")
    private final String name;

    @Schema(description = "아이템 가격", example = "2000")
    private final int price;

    public ItemResponse(Item item) {
        this.name = item.getName();
        this.price = item.getPrice();
    }

    public static ItemResponse from(Item item) {
        return new ItemResponse(item);
    }
}