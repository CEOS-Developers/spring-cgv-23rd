package com.ceos.spring_boot.domain.payment.client;

import com.ceos.spring_boot.domain.payment.dto.PaymentApiWrapper;
import com.ceos.spring_boot.domain.payment.dto.PaymentRequest;
import com.ceos.spring_boot.domain.payment.dto.PaymentResponse;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.config.PaymentConfig;
import com.ceos.spring_boot.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestClient paymentRestClient;
    private final PaymentConfig paymentConfig;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;


    // 결제 요청
    public PaymentResponse requestPayment(String paymentId, PaymentRequest request) {
        log.info("[PaymentClient] 즉시 결제 요청 - ID: {}", paymentId);

        String customDataStr;
        try {
            customDataStr = objectMapper.writeValueAsString(request.customData());
        } catch (Exception e) {
            log.error("[PaymentClient] customData JSON 변환 실패 - storeId: {}", request.storeId(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Map<String, Object> externalBody = new HashMap<>();
        externalBody.put("storeId", request.storeId());
        externalBody.put("orderName", request.orderName());
        externalBody.put("totalPayAmount", request.totalPayAmount());
        externalBody.put("currency", request.currency());
        externalBody.put("customData", customDataStr);

        return paymentRestClient.post()
                .uri("/payments/{paymentId}/instant", paymentId)
                .body(externalBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    log.error("[PaymentClient] 결제 실패 - Status: {}, ID: {}", res.getStatusCode(), paymentId);
                    // 500 에러(10% 확률 실패) 등 처리
                    throw new BusinessException(ErrorCode.PAYMENT_FAILED);
                })
                .body(new ParameterizedTypeReference<PaymentApiWrapper<PaymentResponse>>() {})
                .data();
    }

    // 결제 취소
    public PaymentResponse cancelPayment(String paymentId) {
        log.info("[PaymentClient] 결제 취소 요청 - ID: {}", paymentId);

        return paymentRestClient.post()
                .uri("/payments/{paymentId}/cancel", paymentId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    log.error("[PaymentClient] 취소 실패 - ID: {}", paymentId);
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
                })
                .body(new ParameterizedTypeReference<PaymentApiWrapper<PaymentResponse>>() {})
                .data();
    }

    // 결제 내역 조회
    public PaymentResponse getPaymentDetail(String paymentId) {
        return paymentRestClient.get()
                .uri("/payments/{paymentId}", paymentId)
                .retrieve()
                .body(new ParameterizedTypeReference<PaymentApiWrapper<PaymentResponse>>() {})
                .data();
    }
}
