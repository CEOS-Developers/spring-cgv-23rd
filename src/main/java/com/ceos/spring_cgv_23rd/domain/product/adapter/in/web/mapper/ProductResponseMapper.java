package com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.dto.response.ProductResponse;
import com.ceos.spring_cgv_23rd.domain.product.application.dto.result.OrderDetailResult;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseMapper {

    public ProductResponse.OrderDetailResponse toResponse(OrderDetailResult result) {
        return ProductResponse.OrderDetailResponse.builder()
                .orderId(result.orderId())
                .orderNumber(result.orderNumber())
                .status(result.status())
                .theaterName(result.theaterName())
                .items(result.items().stream()
                        .map(item -> ProductResponse.OrderItemInfo.builder()
                                .productId(item.productId())
                                .productName(item.productName())
                                .quantity(item.quantity())
                                .price(item.price())
                                .totalPrice(item.itemTotalPrice())
                                .build())
                        .toList())
                .totalPrice(result.totalPrice())
                .createdAt(result.createdAt())
                .build();
    }
}
