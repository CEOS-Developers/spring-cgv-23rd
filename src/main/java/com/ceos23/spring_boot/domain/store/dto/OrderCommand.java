package com.ceos23.spring_boot.domain.store.dto;

import java.util.List;

public record OrderCommand(
        Long userId,
        Long theaterId,
        List<OrderItemCommand> orderItems
){}
