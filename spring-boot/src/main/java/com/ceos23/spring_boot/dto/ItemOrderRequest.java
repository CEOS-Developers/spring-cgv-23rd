package com.ceos23.spring_boot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrderRequest {

    private Long userId;
    private Long theaterId;
    private List<OrderItemRequest> items;
}