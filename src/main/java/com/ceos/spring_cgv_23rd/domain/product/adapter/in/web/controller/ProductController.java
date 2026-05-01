package com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.controller;

import com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.dto.request.ProductRequest;
import com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.dto.response.ProductResponse;
import com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.mapper.ProductRequestMapper;
import com.ceos.spring_cgv_23rd.domain.product.adapter.in.web.mapper.ProductResponseMapper;
import com.ceos.spring_cgv_23rd.domain.product.application.dto.result.OrderDetailResult;
import com.ceos.spring_cgv_23rd.domain.product.application.port.in.CancelOrderUseCase;
import com.ceos.spring_cgv_23rd.domain.product.application.port.in.CreateOrderUseCase;
import com.ceos.spring_cgv_23rd.global.annotation.LoginUser;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "상품 관련 API")
public class ProductController {

    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final ProductRequestMapper productRequestMapper;
    private final ProductResponseMapper productResponseMapper;

    @Operation(summary = "매점 주문")
    @PostMapping("/order")
    public ApiResponse<ProductResponse.OrderDetailResponse> createOrder(
            @LoginUser Long userId,
            @RequestHeader(value = "Idempotency-Key") @NotBlank String idempotencyKey,
            @Valid @RequestBody ProductRequest.CreateOrderRequest request) {
        OrderDetailResult result = createOrderUseCase.createOrder(userId, productRequestMapper.toCommand(idempotencyKey, request));
        ProductResponse.OrderDetailResponse response = productResponseMapper.toResponse(result);

        return ApiResponse.onSuccess("매점 주문 성공", response);
    }

    @Operation(summary = "매점 주문 취소")
    @PatchMapping("/order/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(
            @LoginUser Long userId,
            @PathVariable Long orderId) {
        cancelOrderUseCase.cancelOrder(userId, orderId);
        return ApiResponse.onSuccess("매점 주문 취소 성공");
    }
}
