package com.ceos23.spring_cgv_23rd.Payment.Service;

import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentRequestDTO;
import com.ceos23.spring_cgv_23rd.Payment.DTO.PaymentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "concurrencyClient", url = "https://ceos.diggindie.com")
public interface ConcurrencyClient {

    @PostMapping("/payments/{paymentId}/instant")
    PaymentResponseDTO pay(@PathVariable("paymentId") String paymentId,
                           @RequestBody PaymentRequestDTO req);

    @PostMapping("/payments/{paymentId}/cancel")
    PaymentResponseDTO cancel(@PathVariable("paymentId") String paymentId);

    @GetMapping("/payments/{paymentId}")
    PaymentResponseDTO checkPayment(@PathVariable("paymentId") String paymentId);
}