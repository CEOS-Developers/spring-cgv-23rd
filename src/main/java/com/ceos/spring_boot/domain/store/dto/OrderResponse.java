package com.ceos.spring_boot.domain.store.dto;

import com.ceos.spring_boot.domain.store.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OrderResponse(

        @Schema(description = "주문 id", example = "7")
        Long orderId,

        @Schema(description = "총 가격(원)", example = "12000")
        Integer totalPrice,

        @Schema(description = "주문 상태", example = "COMPLETED")
        String status,

        @Schema(description = "주문 상세 내역")
        List<OrderItemResponse> items

) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus().getDescription(),
                order.getOrderItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
