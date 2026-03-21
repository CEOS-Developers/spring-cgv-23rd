package com.ceos.spring_cgv_23rd.domain.product.controller;

import com.ceos.spring_cgv_23rd.domain.product.dto.ProductRequestDTO;
import com.ceos.spring_cgv_23rd.domain.product.dto.ProductResponseDTO;
import com.ceos.spring_cgv_23rd.domain.product.service.ProductService;
import com.ceos.spring_cgv_23rd.global.apiPayload.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product", description = "상품 관련 API")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "매점 주문")
    @PostMapping("/order")
    public ApiResponse<ProductResponseDTO.OrderDetailResponseDTO> createOrder(
            @Valid @RequestBody ProductRequestDTO.CreateOrderRequestDTO request) {       // TODO: userId 추가
        ProductResponseDTO.OrderDetailResponseDTO response = productService.createOrder(1L, request);
        return ApiResponse.onSuccess("매점 주문 성공", response);
    }

    @Operation(summary = "매점 주문 취소")
    @PatchMapping("/order/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(
            @PathVariable Long orderId) {       // TODO: userId 추가
        productService.cancelOrder(1L, orderId);
        return ApiResponse.onSuccess("매점 주문 취소 성공");
    }
}
