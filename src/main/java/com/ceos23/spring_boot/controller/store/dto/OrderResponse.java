package com.ceos23.spring_boot.controller.store.dto;

import com.ceos23.spring_boot.domain.store.dto.OrderInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "매점 주문 최종 응답 데이터")
public record OrderResponse(
        @Schema(description = "주문 내역 고유 ID", example = "1")
        Long orderId,

        @Schema(description = "메뉴 주문한 영화관 지점명", example = "CGV 강남점")
        String theaterName,

        @Schema(description = "총 결제 금액", example = "10000")
        Integer totalPrice,

        @Schema(description = "결제된 메뉴 목록")
        List<OrderItemResponse> orderItemResponses
) {
    public static OrderResponse from(OrderInfo info) {
        return new OrderResponse(
                info.orderId(),
                info.theaterName(),
                info.totalPrice(),
                info.orderItemInfos().stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}
