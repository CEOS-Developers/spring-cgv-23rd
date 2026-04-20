package com.ceos23.cgv_clone.store.service.impl;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.store.dto.request.OrderRequest;
import com.ceos23.cgv_clone.store.dto.response.InventoryResponse;
import com.ceos23.cgv_clone.store.dto.response.OrderResponse;
import com.ceos23.cgv_clone.store.repository.LockRepository;
import com.ceos23.cgv_clone.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("orderServiceNamed")
@RequiredArgsConstructor
public class OrderServiceNamed implements OrderService {

    private static final int LOCK_TIMEOUT_SECONDS = 3;

    private final LockRepository lockRepository;
    private final OrderServiceNamedInner inner;

    @Override
    public OrderResponse createOrder(Long userId, Long storeId, OrderRequest request) {
        String key = "store:" + storeId;

        boolean locked = lockRepository.getLock(key, LOCK_TIMEOUT_SECONDS);
        if (!locked) {
            throw new CustomException(ErrorCode.CONCURRENT_UPDATE_FAILED);
        }

        try {
            return inner.createOrder(userId, storeId, request);
        } finally {
            lockRepository.releaseLock(key);
        }
    }

    @Override
    public List<InventoryResponse> getInventories(Long storeId) {
        return inner.getInventories(storeId);
    }
}
