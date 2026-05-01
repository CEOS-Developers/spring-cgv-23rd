package com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.dto.response;

import com.ceos.spring_cgv_23rd.domain.product.domain.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ProductResponse {

    @Builder
    public record OrderDetailResponse(
            Long orderId,
            String orderNumber,
            OrderStatus status,
            String theaterName,
            List<OrderItemInfo> items,
            Integer totalPrice,
            LocalDateTime createdAt,
            PaymentInfo payment
    ) {
    }

    @Builder
    public record OrderItemInfo(
            Long productId,
            String productName,
            Integer quantity,
            Integer price,
            Integer totalPrice
    ) {
    }


    @Builder
    public record PaymentInfo(
            String paymentId,
            String status,
            Integer amount,
            String orderName,
            String pgProvider,
            LocalDateTime paidAt
    ) {
    }
}
