package com.ceos23.spring_boot.controller.payment;

import com.ceos23.spring_boot.controller.payment.dto.PaymentCreateRequest;
import com.ceos23.spring_boot.controller.payment.dto.PaymentResponse;
import com.ceos23.spring_boot.domain.payment.dto.PaymentDataInfo;
import com.ceos23.spring_boot.domain.payment.service.PaymentService;
import com.ceos23.spring_boot.global.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 및 예매 확정/취소 API")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "결제 내역 조회",
            description = "결제 ID를 통해 결제된 상세 내역을 조회합니다."
    )
    @GetMapping("/api/payments/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(
            @PathVariable
            @Parameter(description = "결제 고유 ID", required = true, example = "20260409_a1b2c3d4")
            String paymentId
    ) {
        PaymentDataInfo info = paymentService.getPaymentDetails(paymentId);
        return ResponseEntity.ok(PaymentResponse.from(info));
    }
}