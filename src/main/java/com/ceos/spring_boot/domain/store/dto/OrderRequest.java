package com.ceos.spring_boot.domain.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OrderRequest(

        @Schema(description = "상품을 구매할 영화관 id", example = "1")
        Long cinemaId,

        @Schema(description = "구매 상세")
        List<OrderItemRequest> orderItems
) {}