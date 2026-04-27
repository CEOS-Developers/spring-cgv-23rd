package com.ceos23.spring_boot.controller.store.dto;

import com.ceos23.spring_boot.domain.store.dto.OrderItemInfo;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "매점 상품 주문 상세 응답 데이터")
public record OrderItemResponse(
        @Schema(description = "메뉴 이름", example = "팝콘")
        String menuName,

        @Schema(description = "결제 메뉴 1개당 가격", example = "5000")
        Integer orderPrice,

        @Schema(description = "주문 수량", example = "1")
        Integer count
) {
    public static OrderItemResponse from(OrderItemInfo info) {
        return new OrderItemResponse(
                info.menuName(),
                info.orderPrice(),
                info.count()
        );
    }
}
