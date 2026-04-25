package com.ceos23.spring_boot.cgv.controller.payment;

import com.ceos23.spring_boot.cgv.dto.payment.PaymentResponse;
import com.ceos23.spring_boot.cgv.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable String paymentId) {
        return PaymentResponse.from(paymentService.getPayment(paymentId));
    }
}
