package com.ceos.spring_boot.domain.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderItemRequest(

        @Schema(description = "상품 id", example = "10")
        Long productId,

        @Schema(description = "구매 할 상품의 개수", example = "2")
        Integer count
) {}
