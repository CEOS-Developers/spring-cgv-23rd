package com.cgv.spring_boot.domain.store.controller;

import com.cgv.spring_boot.domain.store.dto.request.StoreOrderRequest;
import com.cgv.spring_boot.domain.store.service.StoreService;
import com.cgv.spring_boot.global.common.response.ApiResponse;
import com.cgv.spring_boot.global.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Store", description = "매점 주문 관련 API")
@RestController
@RequestMapping("/api/store/orders")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "매점 구매", description = "로그인한 사용자가 특정 영화관 매점 상품을 구매합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> order(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody StoreOrderRequest request
    ) {
        Long orderId = storeService.order(authenticatedUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(orderId));
    }
}
