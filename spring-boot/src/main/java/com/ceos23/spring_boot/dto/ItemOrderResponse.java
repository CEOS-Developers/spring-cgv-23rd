package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.ItemOrder;
import com.ceos23.spring_boot.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ItemOrderResponse {

    private Long orderId;
    private Long userId;
    private Long theaterId;
    private String theaterName;
    private Integer totalPrice;
    private LocalDateTime orderedAt;
    private OrderStatus orderStatus;
    private String paymentId;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private List<OrderDetailResponse> orderDetails;

    public static ItemOrderResponse from(ItemOrder itemOrder) {
        return ItemOrderResponse.builder()
                .orderId(itemOrder.getId())
                .userId(itemOrder.getUser().getId())
                .theaterId(itemOrder.getTheater().getId())
                .theaterName(itemOrder.getTheater().getName())
                .totalPrice(itemOrder.getTotalPrice())
                .orderedAt(itemOrder.getOrderedAt())
                .orderStatus(itemOrder.getOrderStatus())
                .paymentId(itemOrder.getPaymentId())
                .paidAt(itemOrder.getPaidAt())
                .cancelledAt(itemOrder.getCancelledAt())
                .orderDetails(
                        itemOrder.getOrderDetails().stream()
                                .map(OrderDetailResponse::from)
                                .toList()
                )
                .build();
    }
}