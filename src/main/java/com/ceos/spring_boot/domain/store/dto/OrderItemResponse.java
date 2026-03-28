package com.ceos.spring_boot.domain.store.dto;

import com.ceos.spring_boot.domain.store.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;

public record OrderItemResponse(

        @Schema(description = "주문 상세 id", example = "1")
        Long orderItemId,

        @Schema(description = "상품 이름", example = "팝콘")
        String productName,

        @Schema(description = "상품 가격", example = "6000")
        Integer price,

        @Schema(description = "구매 상품 수량", example = "2")
        Integer count,

        @Schema(description = "해당 품목 합계 가격(원)", example = "12000")
        Integer subTotal
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getCount(),
                item.getProduct().getPrice() * item.getCount()
        );
    }
}
