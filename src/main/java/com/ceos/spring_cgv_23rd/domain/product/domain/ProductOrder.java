package com.ceos.spring_cgv_23rd.domain.product.domain;

import com.ceos.spring_cgv_23rd.domain.product.exception.ProductErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ProductOrder {

    private Long id;
    private Long userId;
    private Long theaterId;
    private String orderNumber;
    private Integer totalPrice;
    private OrderStatus status;
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
    private LocalDateTime createdAt;

    public static ProductOrder createOrder(Long userId, Long theaterId, List<OrderItem> orderItems) {

        int totalPrice = orderItems.stream()
                .mapToInt(item -> item.getQuantity() * item.getPrice())
                .sum();

        ProductOrder order = ProductOrder.builder()
                .userId(userId)
                .theaterId(theaterId)
                .orderNumber(generateOrderNumber())
                .totalPrice(totalPrice)
                .status(OrderStatus.COMPLETED)
                .build();

        orderItems.forEach(order::addOrderItem);

        return order;
    }

    private static String generateOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return datePart + "-" + randomPart;
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new GeneralException(ProductErrorCode.ALREADY_CANCELLED);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }
}
