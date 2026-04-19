package com.ceos23.cgv_clone.store.dto.response;

import com.ceos23.cgv_clone.store.entity.Inventory;
import com.ceos23.cgv_clone.store.entity.MenuType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InventoryResponse {
    private Long id;
    private int quantity;

    private int price;
    private String menuName;
    private MenuType menuType;

    public static InventoryResponse from(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .quantity(inventory.getQuantity())
                .price(inventory.getMenu().getPrice())
                .menuName(inventory.getMenu().getName())
                .menuType(inventory.getMenu().getMenuType())
                .build();
    }

}
