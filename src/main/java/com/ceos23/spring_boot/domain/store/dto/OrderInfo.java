package com.ceos23.spring_boot.domain.store.dto;

import com.ceos23.spring_boot.domain.store.entity.Order;
import com.ceos23.spring_boot.domain.store.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public record OrderInfo(
        Long orderId,
        String theaterName,
        Integer totalPrice,
        List<OrderItemInfo> orderItemInfos) {

    public static OrderInfo from(Order order, List<OrderItem> orderItems) {
        return new OrderInfo(
                order.getId(),
                order.getTheater().getName(),
                order.getTotalPrice(),
                orderItems.stream()
                        .map(OrderItemInfo::from)
                        .toList()
        );
    }
}
