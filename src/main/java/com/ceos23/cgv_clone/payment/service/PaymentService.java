package com.ceos23.cgv_clone.payment.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.payment.client.PaymentFeignClient;
import com.ceos23.cgv_clone.payment.dto.request.PaymentRequest;
import com.ceos23.cgv_clone.payment.dto.response.PaymentApiResponse;
import com.ceos23.cgv_clone.payment.dto.response.PaymentResponse;
import com.ceos23.cgv_clone.payment.entity.Currency;
import com.ceos23.cgv_clone.payment.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentFeignClient paymentFeignClient;

    @Value("${payment.store-id}")
    private String storeId;

    public PaymentResponse pay(String paymentId, String orderName, int totalAmount) {
        PaymentRequest request = PaymentRequest.builder()
                .storeId(storeId)
                .orderName(orderName)
                .totalPayAmount(totalAmount)
                .currency(Currency.KRW)
                .build();

        PaymentApiResponse response = paymentFeignClient.pay(paymentId, request);

        if (response == null || response.getPayload() == null) {
            throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
        }
        PaymentResponse payload = response.getPayload();
        if (payload.getPaymentStatus() != PaymentStatus.PAID) {
            log.warn("결제 실패 응답 paymentId={}, status={}", paymentId, payload.getPaymentStatus());
            throw new CustomException(ErrorCode.PAYMENT_FAILED);
        }
        return payload;
    }

    public void cancel(String paymentId) {
        PaymentApiResponse response = paymentFeignClient.cancel(paymentId);
        if (response == null || response.getPayload() == null) {
            throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
        }
        PaymentResponse payload = response.getPayload();
        if (payload.getPaymentStatus() != PaymentStatus.CANCELLED) {
            throw new CustomException(ErrorCode.PAYMENT_CANCELLED_FAILED);
        }
    }

    public PaymentResponse find(String paymentId) {
        PaymentApiResponse response = paymentFeignClient.find(paymentId);
        if (response == null || response.getPayload() == null) {
            throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
        }
        return response.getPayload();
    }
}
