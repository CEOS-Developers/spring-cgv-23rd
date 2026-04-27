package com.ceos23.spring_boot.infra.payment.client;

import com.ceos23.spring_boot.infra.payment.dto.PaymentApiResponse;
import com.ceos23.spring_boot.infra.payment.dto.PaymentAuthData;
import com.ceos23.spring_boot.infra.payment.dto.PaymentData;
import com.ceos23.spring_boot.infra.payment.dto.PaymentInstantRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "paymentFeignClient",
        url = "${payment.base-url}"
)
public interface PaymentFeignClient {

    @GetMapping("/auth/{githubId}")
    PaymentApiResponse<PaymentAuthData> getApiSecret(
            @PathVariable String githubId
    );

    @PostMapping("/payments/{paymentId}/instant")
    PaymentApiResponse<PaymentData> instantPayment(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String paymentId,
            @RequestBody PaymentInstantRequest request
    );

    @PostMapping("/payments/{paymentId}/cancel")
    PaymentApiResponse<PaymentData> cancelPayment(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String paymentId
    );

    @GetMapping("/payments/{paymentId}")
    PaymentApiResponse<PaymentData> getPayment(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String paymentId
    );
}