package com.cgv.spring_boot.domain.payment.service;

import com.cgv.spring_boot.domain.payment.dto.request.PaymentCreateRequest;
import com.cgv.spring_boot.domain.payment.dto.response.PaymentResponse;
import com.cgv.spring_boot.domain.payment.exception.PaymentErrorCode;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PortOnePaymentClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final RestClient.Builder restClientBuilder;

    @Value("${payment.base-url}")
    private String baseUrl;

    @Value("${payment.store-id}")
    private String storeId;

    // PortOne auth 응답을 재사용해 불필요한 인증 호출을 줄임
    private volatile String apiSecretKey;

    public PaymentResponse instantPay(String paymentId, PaymentCreateRequest request) {
        return execute(() -> {
            String responseBody = restClient().post()
                    .uri("/payments/{paymentId}/instant", paymentId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ExternalInstantPaymentRequest(
                            storeId,
                            request.orderName(),
                            request.totalPayAmount(),
                            request.currency(),
                            request.customData()
                    ))
                    .retrieve()
                    .body(String.class);

            ExternalApiResponse<ExternalPaymentData> response = parseResponse(
                    responseBody,
                    new ParameterizedTypeReference<>() {
                    },
                    "instantPay"
            );

            return toPaymentResponse(response);
        }, PaymentAction.INSTANT_PAY);
    }

    public PaymentResponse cancel(String paymentId) {
        return execute(() -> {
            String responseBody = restClient().post()
                    .uri("/payments/{paymentId}/cancel", paymentId)
                    .header(HttpHeaders.AUTHORIZATION, bearerToken())
                    .retrieve()
                    .body(String.class);

            ExternalApiResponse<ExternalPaymentData> response = parseResponse(
                    responseBody,
                    new ParameterizedTypeReference<>() {
                    },
                    "cancel"
            );

            return toPaymentResponse(response);
        }, PaymentAction.CANCEL);
    }

    private String bearerToken() {
        return "Bearer " + getApiSecretKey();
    }

    private String getApiSecretKey() {
        if (apiSecretKey != null) {
            return apiSecretKey;
        }

        synchronized (this) {
            if (apiSecretKey == null) {
                // storeId 기반 auth 응답에서 API secret key를 한 번만 조회한다.
                ExternalApiResponse<ExternalAuthData> response = execute(() -> {
                            String responseBody = restClient().get()
                                    .uri("/auth/{githubId}", storeId)
                                    .retrieve()
                                    .body(String.class);
                            return parseResponse(
                                    responseBody,
                                    new ParameterizedTypeReference<>() {
                                    },
                                    "auth"
                            );
                        }, PaymentAction.AUTH);

                if (response == null || response.data() == null || response.data().apiSecretKey() == null) {
                    throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_RESPONSE);
                }
                apiSecretKey = response.data().apiSecretKey();
            }
        }

        return apiSecretKey;
    }

    private PaymentResponse toPaymentResponse(ExternalApiResponse<ExternalPaymentData> response) {
        if (response == null || response.data() == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_RESPONSE);
        }

        ExternalPaymentData data = response.data();
        return new PaymentResponse(
                data.paymentId(),
                data.paymentStatus(),
                data.orderName(),
                data.pgProvider(),
                data.currency(),
                data.customData(),
                data.paidAt()
        );
    }

    private RestClient restClient() {
        return restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    private <T> T parseResponse(String responseBody, ParameterizedTypeReference<T> typeReference, String context) {
        if (responseBody == null || responseBody.isBlank()) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_RESPONSE);
        }

        try {
            return OBJECT_MAPPER.readValue(
                    responseBody,
                    OBJECT_MAPPER.getTypeFactory().constructType(typeReference.getType())
            );
        } catch (JsonProcessingException e) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_RESPONSE);
        }
    }

    // 외부 API 예외를 도메인 예외로 일관되게 변환
    private <T> T execute(PaymentCall<T> paymentCall, PaymentAction action) {
        try {
            return paymentCall.execute();
        } catch (RestClientResponseException e) {
            throw mapException(action, e.getStatusCode().value());
        } catch (ResourceAccessException e) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_SERVER_UNAVAILABLE);
        } catch (RestClientException e) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_RESPONSE);
        }
    }

    private BusinessException mapException(PaymentAction action, int statusCode) {
        if (statusCode == 404 && action == PaymentAction.INSTANT_PAY) {
            return new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
        if (statusCode == 409 && action == PaymentAction.CANCEL) {
            return new BusinessException(PaymentErrorCode.PAYMENT_NOT_CANCELLABLE);
        }
        if (statusCode >= 500) {
            return new BusinessException(PaymentErrorCode.PAYMENT_FAILED);
        }
        return new BusinessException(PaymentErrorCode.INVALID_PAYMENT_RESPONSE);
    }

    @FunctionalInterface
    private interface PaymentCall<T> {
        T execute();
    }

    private enum PaymentAction {
        AUTH,
        INSTANT_PAY,
        CANCEL
    }

    // PortOne 즉시결제 요청 바디
    private record ExternalInstantPaymentRequest(
            String storeId,
            String orderName,
            Integer totalPayAmount,
            String currency,
            String customData
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ExternalApiResponse<T>(
            @JsonAlias("status")
            Integer code,
            String message,
            @JsonAlias("payload")
            T data
    ) {
    }

    // PortOne auth API의 data 영역
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ExternalAuthData(
            String githubId,
            String apiSecretKey,
            LocalDateTime createdAt
    ) {
    }

    // PortOne 결제 API의 data 영역
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ExternalPaymentData(
            String paymentId,
            String paymentStatus,
            String orderName,
            String pgProvider,
            String currency,
            String customData,
            LocalDateTime paidAt
    ) {
    }
}
