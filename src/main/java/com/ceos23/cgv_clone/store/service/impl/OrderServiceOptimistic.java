package com.ceos23.cgv_clone.store.service.impl;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.store.dto.request.OrderRequest;
import com.ceos23.cgv_clone.store.dto.response.InventoryResponse;
import com.ceos23.cgv_clone.store.dto.response.OrderResponse;
import com.ceos23.cgv_clone.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("orderServiceOptimistic")
@RequiredArgsConstructor
public class OrderServiceOptimistic implements OrderService {

    private static final int MAX_RETRY = 5;
    private static final long RETRY_DELAY_MS = 50;

    private final OrderServiceOptimisticInner inner;

    @Override
    public OrderResponse createOrder(Long userId, Long storeId, OrderRequest request) {
        int attempt = 0;
        while (true) {
            try {
                return inner.createOrder(userId, storeId, request);
            } catch (ObjectOptimisticLockingFailureException e) {
                attempt++;
                if (attempt >= MAX_RETRY) {
                    throw new CustomException(ErrorCode.CONCURRENT_UPDATE_FAILED);
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new CustomException(ErrorCode.CONCURRENT_UPDATE_FAILED);
                }
            }
        }
    }

    @Override
    public List<InventoryResponse> getInventories(Long storeId) {
        return inner.getInventories(storeId);
    }
}
