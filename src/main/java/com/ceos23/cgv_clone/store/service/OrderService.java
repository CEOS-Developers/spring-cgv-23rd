package com.ceos23.cgv_clone.store.service;

import com.ceos23.cgv_clone.store.dto.request.OrderRequest;
import com.ceos23.cgv_clone.store.dto.response.InventoryResponse;
import com.ceos23.cgv_clone.store.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(Long userId, Long storeId, OrderRequest request);

    List<InventoryResponse> getInventories(Long storeId);

    default OrderResponse cancelOrder(Long userId, Long orderId) {
        throw new UnsupportedOperationException("해당 구현체에서는 주문 취소를 지원하지 않습니다.");
    }
}
