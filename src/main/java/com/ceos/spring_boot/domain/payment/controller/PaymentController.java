package com.ceos.spring_boot.domain.payment.controller;

import com.ceos.spring_boot.domain.payment.dto.PaymentRequest;
import com.ceos.spring_boot.domain.payment.dto.PaymentResponse;
import com.ceos.spring_boot.domain.payment.service.PaymentService;
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

@Tag(name = "Payment 관련 API", description = "결제 및 취소를 위한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제하기", description = "영화 티켓 및 매점 상품을 결제합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentRequest paymentRequest
    ) {
        // 서비스에서 카테고리에 따라 내부 로직 분기 처리
        PaymentResponse response = paymentService.createPayment(
                userDetails.getId(),
                paymentRequest,
                paymentRequest.category(),
                paymentRequest.targetId()
        );

        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.PAYMENT_SUCCESS));
    }

    @Operation(summary = "결제 내역 조회", description = "결제 고유 ID로 상세 내역을 조회합니다.")
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentDetail(
            @PathVariable String paymentId
    ) {
        PaymentResponse response = paymentService.getPaymentDetail(paymentId);
        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.GET_PAYMENT_SUCCESS));
    }

    @Operation(summary = "결제 취소 하기", description = "결제를 취소합니다.")
    @PostMapping("/{paymentId}/cancel") // 결제 고유 ID를 경로로 받음
    public ResponseEntity<ApiResponse<PaymentResponse>> cancelPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String paymentId
    ) {
        // 보안상 본인의 결제인지 확인하는 로직이 서비스 내부에 포함되어야 함
        PaymentResponse response = paymentService.cancelPayment(paymentId);

        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.CANCEL_SUCCESS));
    }
}
