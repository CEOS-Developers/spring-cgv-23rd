package com.ceos.spring_cgv_23rd.domain.product.dto;

import com.ceos.spring_cgv_23rd.domain.product.entity.OrderItem;
import com.ceos.spring_cgv_23rd.domain.product.entity.ProductOrder;
import com.ceos.spring_cgv_23rd.domain.product.enums.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ProductResponseDTO {

    @Builder
    public record OrderDetailResponseDTO(
            Long orderId,
            String orderNumber,
            OrderStatus status,
            String theaterName,
            List<OrderItemInfoDTO> items,
            Integer totalPrice,
            LocalDateTime createdAt
    ) {
        public static OrderDetailResponseDTO of(ProductOrder order) {
            return OrderDetailResponseDTO.builder()
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .status(order.getStatus())
                    .theaterName(order.getTheater().getName())
                    .items(order.getOrderItems().stream()
                            .map(OrderItemInfoDTO::from)
                            .toList())
                    .totalPrice(order.getTotalPrice())
                    .createdAt(order.getCreatedAt())
                    .build();
        }
    }

    @Builder
    private record OrderItemInfoDTO(
            Long productId,
            String productName,
            Integer quantity,
            Integer price,
            Integer totalPrice
    ) {
        private static OrderItemInfoDTO from(OrderItem item) {
            return OrderItemInfoDTO.builder()
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .totalPrice(item.getPrice() * item.getQuantity())
                    .build();
        }
    }
}
