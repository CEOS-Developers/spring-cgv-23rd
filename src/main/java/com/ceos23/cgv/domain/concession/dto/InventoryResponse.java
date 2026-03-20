package com.ceos23.cgv.domain.concession.dto;

import com.ceos23.cgv.domain.concession.entity.Inventory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryResponse {
    private Long inventoryId;
    private String cinemaName;
    private String productName;
    private int stockQuantity;

    public static InventoryResponse from(Inventory inventory) {
        return InventoryResponse.builder()
                .inventoryId(inventory.getId())
                .cinemaName(inventory.getCinema().getName())
                .productName(inventory.getProduct().getName())
                .stockQuantity(inventory.getStockQuantity())
                .build();
    }
}