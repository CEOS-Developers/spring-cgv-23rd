package com.ceos23.spring_boot.infra.payment.client;

import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.infra.payment.PaymentProperties;
import com.ceos23.spring_boot.infra.payment.dto.PaymentApiResponse;
import com.ceos23.spring_boot.infra.payment.dto.PaymentAuthData;
import com.ceos23.spring_boot.infra.payment.dto.PaymentData;
import com.ceos23.spring_boot.infra.payment.dto.PaymentInstantRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
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
                    throwPaymentException(response.getStatusCode());
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
                .onStatus(HttpStatusCode::isError, (httpRequest, response) -> {
                    throwPaymentException(response.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    public PaymentApiResponse<PaymentData> cancelPayment(String authorization, String paymentId) {
        return restClient.post()
                .uri("/payments/{paymentId}/cancel", paymentId)
                .header("Authorization", authorization)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (httpRequest, response) -> {
                    throwPaymentException(response.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    public PaymentApiResponse<PaymentData> getPayment(String authorization, String paymentId) {
        return restClient.get()
                .uri("/payments/{paymentId}", paymentId)
                .header("Authorization", authorization)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (httpRequest, response) -> {
                    throwPaymentException(response.getStatusCode());
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    private void throwPaymentException(HttpStatusCode statusCode) {
        if (statusCode == HttpStatus.FORBIDDEN) {
            throw new CustomException(ErrorCode.PAYMENT_FORBIDDEN);
        }

        if (statusCode == HttpStatus.NOT_FOUND) {
            throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        if (statusCode == HttpStatus.CONFLICT) {
            throw new CustomException(ErrorCode.PAYMENT_CONFLICT);
        }

        if (statusCode.is5xxServerError()) {
            throw new CustomException(ErrorCode.PAYMENT_SERVER_ERROR);
        }

        throw new CustomException(ErrorCode.PAYMENT_API_ERROR);
    }
}