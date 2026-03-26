package com.ceos23.spring_boot.controller.store.controller;

import com.ceos23.spring_boot.controller.store.dto.OrderRequest;
import com.ceos23.spring_boot.domain.store.dto.OrderInfo;
import com.ceos23.spring_boot.controller.store.dto.OrderResponse;
import com.ceos23.spring_boot.domain.store.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse> createOrder(
            // 변경!!!!!!!!!!!!!!!!!!!
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody OrderRequest request
    ) {
        OrderInfo info = orderService.createOrder(request.toCommand(userId));

        return ResponseEntity.ok(OrderResponse.from(info));
    }
}
