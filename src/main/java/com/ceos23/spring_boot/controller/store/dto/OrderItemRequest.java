package com.ceos23.spring_boot.controller.store.dto;

import com.ceos23.spring_boot.domain.store.dto.OrderItemCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "매점 상품 주문 상세 요청 데이터")
public record OrderItemRequest(
        @Schema(description = "주문할 메뉴 고유 ID", example = "1")
        @NotNull(message = "메뉴 ID는 필수입니다.")
        Long menuId,

        @Schema(description = "주문할 메뉴 주문 수량", example = "1")
        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
        Integer count
) {
    public OrderItemCommand toCommand() {
        return new OrderItemCommand(menuId, count);
    }
}
