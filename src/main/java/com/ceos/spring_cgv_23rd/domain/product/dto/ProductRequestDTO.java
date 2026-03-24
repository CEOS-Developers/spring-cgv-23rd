package com.ceos.spring_cgv_23rd.domain.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

public class ProductRequestDTO {

    @Builder
    public record CreateOrderRequestDTO(
            @NotNull Long theaterId,
            @NotEmpty @Valid List<OrderItemRequestDTO> items
    ) {
    }

    @Builder
    public record OrderItemRequestDTO(
            @NotNull Long productId,
            @NotNull @Min(value = 1, message = "수량은 1 이상이어야 합니다.") Integer quantity
    ) {
    }
}
