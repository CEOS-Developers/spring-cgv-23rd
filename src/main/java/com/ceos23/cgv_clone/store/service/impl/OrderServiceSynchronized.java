package com.ceos23.cgv_clone.store.service.impl;

import com.ceos23.cgv_clone.store.dto.request.OrderRequest;
import com.ceos23.cgv_clone.store.dto.response.InventoryResponse;
import com.ceos23.cgv_clone.store.dto.response.OrderResponse;
import com.ceos23.cgv_clone.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("orderServiceSynchronized")
@RequiredArgsConstructor
public class OrderServiceSynchronized implements OrderService {

    private final OrderServiceSynchronizedInner inner;

    @Override
    public synchronized OrderResponse createOrder(Long userId, Long storeId, OrderRequest request) {
        return inner.createOrder(userId, storeId, request);
    }

    @Override
    public List<InventoryResponse> getInventories(Long storeId) {
        return inner.getInventories(storeId);
    }
}
