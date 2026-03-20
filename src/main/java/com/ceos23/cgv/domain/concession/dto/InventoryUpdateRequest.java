package com.ceos23.cgv.domain.concession.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InventoryUpdateRequest {
    private Long cinemaId;
    private Long productId;
    private int quantity;
}