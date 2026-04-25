package com.ceos23.spring_boot.cgv.controller.payment;

import com.ceos23.spring_boot.cgv.dto.payment.PaymentResponse;
import com.ceos23.spring_boot.cgv.global.security.CustomUserDetails;
import com.ceos23.spring_boot.cgv.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(
            @PathVariable String paymentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return PaymentResponse.from(paymentService.getPayment(paymentId, userDetails.getUserId()));
    }
}
