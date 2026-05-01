package com.ceos.spring_cgv_23rd.domain.product.application.dto.result;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PaymentResult;
import com.ceos.spring_cgv_23rd.domain.product.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResult(
        Long orderId,
        String orderNumber,
        OrderStatus status,
        String theaterName,
        List<OrderItemInfo> items,
        Integer totalPrice,
        LocalDateTime createdAt,
        PaymentResult payment
) {
    public record OrderItemInfo(
            Long productId,
            String productName,
            Integer quantity,
            Integer price,
            Integer itemTotalPrice
    ) {
    }
}
