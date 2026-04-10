package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.dto.ItemOrderRequest;
import com.ceos23.spring_boot.dto.ItemOrderResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.ItemOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ItemOrderController {

    private final ItemOrderService itemOrderService;

    @PostMapping("/orders")
    public ResponseEntity<SuccessResponse<ItemOrderResponse>> orderItems(
            @RequestBody ItemOrderRequest request
    ) {
        ItemOrderResponse response = itemOrderService.orderItems(request);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<SuccessResponse<ItemOrderResponse>> getOrder(
            @PathVariable Long orderId
    ) {
        ItemOrderResponse response = itemOrderService.getOrder(orderId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<SuccessResponse<List<ItemOrderResponse>>> getOrdersByUser(
            @PathVariable Long userId
    ) {
        List<ItemOrderResponse> response = itemOrderService.getOrdersByUser(userId);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", response));
    }
}