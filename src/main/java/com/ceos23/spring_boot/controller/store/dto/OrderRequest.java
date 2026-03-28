package com.ceos23.spring_boot.controller.store.dto;

import com.ceos23.spring_boot.domain.store.dto.OrderCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "매점 주문 요청 데이터")
public record OrderRequest(
        @Schema(description = "결제할 영화관 고유 ID", example = "1")
        @NotNull(message = "영화관 ID는 필수입니다.")
        Long theaterId,

        @Schema(description = "주문할 메뉴 목록")
        @NotEmpty(message = "주문할 메뉴가 하나 이상 존재해야 합니다.")
        @Valid
        List<OrderItemRequest> orderItems
) {
    public OrderCommand toCommand(String email) {
        return new OrderCommand(
                email,
                theaterId,
                orderItems.stream()
                        .map(OrderItemRequest::toCommand)
                        .toList()
        );
    }
}
