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
        long start = System.currentTimeMillis();

        log.info("결제 요청 시작 paymentId={}, totalAmount={}", paymentId, totalAmount);

        PaymentRequest request = PaymentRequest.builder()
                .storeId(storeId)
                .orderName(orderName)
                .totalPayAmount(totalAmount)
                .currency(Currency.KRW)
                .build();

        PaymentApiResponse response;
        try {
            response = paymentFeignClient.pay(paymentId, request);
        } catch (Exception e) {
            log.error("결제 API 호출 실패 paymentId={}, elapsedMs={}", paymentId, System.currentTimeMillis() - start, e);
            throw e;
        }

        if (response == null || response.getPayload() == null) {
            log.error("결제 응답 payload 없음 paymentId={}, elapsedMs={}", paymentId, System.currentTimeMillis() - start);
            throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
        }

        PaymentResponse payload = response.getPayload();

        if (payload.getPaymentStatus() != PaymentStatus.PAID) {
            log.warn("결제 실패 응답 paymentId={}, status={}, elapseMs={}", paymentId, payload.getPaymentStatus(), System.currentTimeMillis() - start);
            throw new CustomException(ErrorCode.PAYMENT_FAILED);
        }

        log.info("결제 성공 paymentId={}, status={}, elapsedMs={}", paymentId, payload.getPaymentStatus(), System.currentTimeMillis() - start);

        return payload;
    }

    public void cancel(String paymentId) {
        long start = System.currentTimeMillis();

        log.info("결제 취소 요청 시작 paymentId={}", paymentId);

        PaymentApiResponse response;
        try {
            response = paymentFeignClient.cancel(paymentId);
        } catch (Exception e) {
            log.error("결제 취소 API 호출 실패 paymentId={}, elapsedMs={}", paymentId, System.currentTimeMillis() - start, e);
            throw e;
        }

        if (response == null || response.getPayload() == null) {
            log.error("결제 취소 응답 payload 없음 paymentId={}, elapsedMs={}", paymentId, System.currentTimeMillis() - start);
            throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
        }

        PaymentResponse payload = response.getPayload();

        if (payload.getPaymentStatus() != PaymentStatus.CANCELLED) {
            log.warn("결제 취소 실패 응답 paymentId={}, status={}, elapseMs={}", paymentId, payload.getPaymentStatus(), System.currentTimeMillis() - start);
            throw new CustomException(ErrorCode.PAYMENT_CANCELLED_FAILED);
        }

        log.info("결제 취소 성공 paymentId={}, status={}, elapsedMs={}", paymentId, payload.getPaymentStatus(), System.currentTimeMillis() - start);
    }

    public PaymentResponse find(String paymentId) {
        long start = System.currentTimeMillis();

        log.info("결제 조회 요청 시작 paymentId={}", paymentId);

        PaymentApiResponse response;
        try {
            response = paymentFeignClient.find(paymentId);
        } catch (Exception e) {
            log.error("결제 조회 API 호출 실패 paymentId={}, elapsedMs={}", paymentId, System.currentTimeMillis() - start, e);
            throw e;
        }

        if (response == null || response.getPayload() == null) {
            log.error("결제 조회 응답 payload 없음 paymentId={}, elapsedMs={}", paymentId, System.currentTimeMillis() - start);
            throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
        }

        PaymentResponse payload = response.getPayload();

        log.info("결제 조회 성공 paymentId={}, status={}, elapsedMs={}", paymentId, payload.getPaymentStatus(), System.currentTimeMillis() - start);
        return payload;
    }
}
