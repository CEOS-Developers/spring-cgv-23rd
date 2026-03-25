package com.cgv.spring_boot.domain.store.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record StoreOrderRequest(
        @NotNull(message = "영화관 ID는 필수입니다.")
        Long theaterId,

        @Valid
        @NotEmpty(message = "주문 상품은 1개 이상이어야 합니다.")
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull(message = "상품 ID는 필수입니다.")
            Long itemId,

            @Positive(message = "주문 수량은 1개 이상이어야 합니다.")
            int count
    ) {
    }
}
