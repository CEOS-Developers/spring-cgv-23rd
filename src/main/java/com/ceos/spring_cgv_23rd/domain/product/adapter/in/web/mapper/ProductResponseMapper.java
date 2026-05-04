package com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.mapper;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PaymentResult;
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
                .payment(toPaymentInfo(result.payment()))
                .build();
    }

    private ProductResponse.PaymentInfo toPaymentInfo(PaymentResult payment) {
        if (payment == null) return null;
        return ProductResponse.PaymentInfo.builder()
                .paymentId(payment.paymentId())
                .status(payment.status().name())
                .amount(payment.amount())
                .orderName(payment.orderName())
                .pgProvider(payment.pgProvider())
                .paidAt(payment.paidAt())
                .build();
    }
}
