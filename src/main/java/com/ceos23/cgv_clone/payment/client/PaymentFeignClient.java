package com.ceos23.cgv_clone.payment.client;

import com.ceos23.cgv_clone.global.config.PaymentFeignConfig;
import com.ceos23.cgv_clone.payment.dto.request.PaymentRequest;
import com.ceos23.cgv_clone.payment.dto.response.PaymentApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "paymentClient",
        url = "${payment.base-url}",
        configuration = PaymentFeignConfig.class
)
public interface PaymentFeignClient {

    @PostMapping("/payments/{paymentId}/instant")
    PaymentApiResponse pay(
            @PathVariable("paymentId") String paymentId,
            @RequestBody PaymentRequest request
    );

    @PostMapping("/payments/{paymentId}/cancel")
    PaymentApiResponse cancel(
            @PathVariable("paymentId") String paymentId
    );

    @GetMapping("/payments/{paymentId}")
    PaymentApiResponse find(
            @PathVariable("paymentId") String paymentId
    );
}
