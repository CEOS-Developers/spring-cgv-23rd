package com.ceos23.spring_boot.domain.store.dto;

import com.ceos23.spring_boot.domain.store.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemInfo(
        String menuName,
        Integer orderPrice,
        Integer count) {
    public static OrderItemInfo from(OrderItem orderItem) {
        return new OrderItemInfo(
                orderItem.getMenu().getName(),
                orderItem.getOrderPrice(),
                orderItem.getCount()
        );
    }
}
