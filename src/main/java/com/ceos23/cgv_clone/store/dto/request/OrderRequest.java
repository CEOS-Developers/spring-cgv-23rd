package com.ceos23.cgv_clone.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderRequest {
    private List<OrderItemRequest> items;

    @Getter
    public static class OrderItemRequest {
        private Long inventoryId;
        private int quantity;
    }
}
