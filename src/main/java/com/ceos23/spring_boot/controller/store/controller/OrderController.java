package com.ceos23.spring_boot.controller.store.controller;

import com.ceos23.spring_boot.controller.store.dto.OrderRequest;
import com.ceos23.spring_boot.domain.store.dto.OrderInfo;
import com.ceos23.spring_boot.controller.store.dto.OrderResponse;
import com.ceos23.spring_boot.domain.store.service.OrderService;
import com.ceos23.spring_boot.global.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "매점 주문 API")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "매점 주문", description = "사용자가 매점 메뉴를 선택하여 주문을 생성하고, 재고를 차감합니다.")
    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody OrderRequest request
    ) {
        OrderInfo info = orderService.createOrder(request.toCommand(customUserDetails.getEmail()));

        return ResponseEntity.ok(OrderResponse.from(info));
    }
}
