package com.ceos23.spring_boot.infra.payment.client;

import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.infra.payment.PaymentProperties;
import com.ceos23.spring_boot.infra.payment.dto.PaymentApiResponse;
import com.ceos23.spring_boot.infra.payment.dto.PaymentAuthData;
import com.ceos23.spring_boot.infra.payment.dto.PaymentData;
import com.ceos23.spring_boot.infra.payment.dto.PaymentInstantRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentRestClient {

    private final RestClient restClient;

    public PaymentRestClient(RestClient.Builder builder, PaymentProperties paymentProperties) {
        this.restClient = builder
                .baseUrl(paymentProperties.getBaseUrl())
                .build();
    }

    public PaymentApiResponse<PaymentAuthData> getApiSecret(String githubId) {
        return restClient.get()
                .uri("/auth/{githubId}", githubId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new CustomException(ErrorCode.PAYMENT_API_ERROR);
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    public PaymentApiResponse<PaymentData> instantPayment(
            String authorization,
            String paymentId,
            PaymentInstantRequest request
    ) {
        return restClient.post()
                .uri("/payments/{paymentId}/instant", paymentId)
                .header("Authorization", authorization)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (httpRequest, response) -> {
                    throw new CustomException(ErrorCode.PAYMENT_BAD_REQUEST);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (httpRequest, response) -> {
                    throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    public PaymentApiResponse<PaymentData> cancelPayment(String authorization, String paymentId) {
        return restClient.post()
                .uri("/payments/{paymentId}/cancel", paymentId)
                .header("Authorization", authorization)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (httpRequest, response) -> {
                    throw new CustomException(ErrorCode.PAYMENT_BAD_REQUEST);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (httpRequest, response) -> {
                    throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    public PaymentApiResponse<PaymentData> getPayment(String authorization, String paymentId) {
        return restClient.get()
                .uri("/payments/{paymentId}", paymentId)
                .header("Authorization", authorization)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (httpRequest, response) -> {
                    throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (httpRequest, response) -> {
                    throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
                })
                .body(new ParameterizedTypeReference<>() {});
    }
}