package com.ceos23.spring_boot.domain.store.dto;

import java.util.List;

public record OrderCommand(
        String email,
        Long theaterId,
        List<OrderItemCommand> orderItems
){}
