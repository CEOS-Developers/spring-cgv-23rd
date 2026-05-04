package com.ceos.spring_cgv_23rd.domain.product.application.dto.command;

import java.util.List;

public record CreateOrderCommand(
        String paymentId,
        Long theaterId,
        List<OrderItemCommand> items
) {
    public record OrderItemCommand(
            Long productId,
            Integer quantity
    ) {
    }
}
