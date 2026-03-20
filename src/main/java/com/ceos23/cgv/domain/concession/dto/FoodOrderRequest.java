package com.ceos23.cgv.domain.concession.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class FoodOrderRequest {
    private Long userId;
    private Long cinemaId;
    private List<OrderItemRequest> orderItems;

    // 어떤 상품을 몇 개 살 것인지
    @Getter
    @NoArgsConstructor
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
    }
}