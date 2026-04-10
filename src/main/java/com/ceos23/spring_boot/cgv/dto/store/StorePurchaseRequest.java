package com.ceos23.spring_boot.cgv.dto.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StorePurchaseRequest(
        @NotNull Long cinemaId,
        @NotNull Long storeMenuId,
        @NotNull @Min(1) Integer quantity
) {
}