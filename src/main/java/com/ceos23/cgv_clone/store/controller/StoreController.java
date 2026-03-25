package com.ceos23.cgv_clone.store.controller;

import com.ceos23.cgv_clone.global.response.ApiResponse;
import com.ceos23.cgv_clone.global.response.SuccessCode;
import com.ceos23.cgv_clone.store.dto.request.OrderRequest;
import com.ceos23.cgv_clone.store.dto.response.InventoryResponse;
import com.ceos23.cgv_clone.store.dto.response.OrderResponse;
import com.ceos23.cgv_clone.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final OrderService orderService;

    @GetMapping("/{storeId}/inventories")
    public ApiResponse<List<InventoryResponse>> getInventories(
            @PathVariable Long storeId
    ) {
        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, orderService.getInventories(storeId));
    }

    @PostMapping("/{storeId}/orders")
    public ApiResponse<OrderResponse> createOrder(
            @RequestHeader Long userId,
            @PathVariable Long storeId,
            @RequestBody OrderRequest request
    ) {
        return ApiResponse.ok(SuccessCode.INSERT_SUCCESS, orderService.createOrder(userId, storeId, request));
    }
}
