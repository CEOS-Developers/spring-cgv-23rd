package com.ceos23.spring_boot.domain.store.dto;

public record OrderItemCommand(
        Long menuId,
        Integer count
) {}
