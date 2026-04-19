package com.ceos23.cgv_clone.store.dto.response;

import com.ceos23.cgv_clone.store.entity.Order;
import com.ceos23.cgv_clone.store.entity.OrderItem;
import com.ceos23.cgv_clone.store.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderResponse {
    private Long orderId;
    private int totalPrice;
    private OrderStatus status;
    private List<OrderItemInfo> items;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getOrderStatus())
                .items(order.getOrderItems().stream()
                        .map(OrderItemInfo::from)
                        .toList())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemInfo {
        private String menuName;
        private int quantity;
        private int unitPrice;

        public static OrderItemInfo from(OrderItem item) {
            return OrderItemInfo.builder()
                    .menuName(item.getInventory().getMenu().getName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .build();
        }
    }
}
