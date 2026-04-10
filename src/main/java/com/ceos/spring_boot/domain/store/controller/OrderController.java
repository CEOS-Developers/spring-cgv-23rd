package com.ceos.spring_boot.domain.store.controller;

import com.ceos.spring_boot.domain.store.dto.OrderItemResponse;
import com.ceos.spring_boot.domain.store.dto.OrderRequest;
import com.ceos.spring_boot.domain.store.dto.OrderResponse;
import com.ceos.spring_boot.domain.store.service.StoreService;
import com.ceos.spring_boot.global.codes.SuccessCode;
import com.ceos.spring_boot.global.response.ApiResponse;
import com.ceos.spring_boot.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store 관련 API", description = "매점 주문 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final StoreService storeService;

    @Operation(summary = "매점 상품 주문", description = "영화관 지점별 재고를 확인하여 상품을 주문합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid OrderRequest request
    ) {
        OrderResponse response = storeService.createOrder(userDetails.getUser().getId(), request);
        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.INSERT_SUCCESS));
    }

    @Operation(summary = "주문 상세 품목 조회", description = "특정 주문 번호에 속한 상품 리스트를 조회합니다.")
    @GetMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getOrderItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId
    ) {
        List<OrderItemResponse> responses = storeService.getOrderItems(userDetails.getUser().getId(), orderId);
        return ResponseEntity.ok(ApiResponse.of(responses, SuccessCode.GET_SUCCESS));
    }
}
