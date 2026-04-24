package com.ceos23.cgv.domain.payment.client;

import com.ceos23.cgv.domain.payment.config.PaymentProperties;
import com.ceos23.cgv.domain.payment.dto.PaymentApiResponse;
import com.ceos23.cgv.domain.payment.dto.PaymentInstantRequest;
import com.ceos23.cgv.domain.payment.dto.PaymentResponse;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestClient paymentRestClient;
    private final PaymentProperties paymentProperties;

    public PaymentResponse requestInstantPayment(String paymentId, PaymentInstantRequest request) {
        return executePaymentRequest(() -> paymentRestClient.post()
                .uri("/payments/{paymentId}/instant", paymentId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .body(request)
                .retrieve()
                .body(PaymentApiResponse.class), ErrorCode.PAYMENT_FAILED);
    }

    public PaymentResponse cancelPayment(String paymentId) {
        return executePaymentRequest(() -> paymentRestClient.post()
                .uri("/payments/{paymentId}/cancel", paymentId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .body(PaymentApiResponse.class), ErrorCode.PAYMENT_CANCEL_FAILED);
    }

    private PaymentResponse executePaymentRequest(PaymentRequestExecutor executor, ErrorCode errorCode) {
        try {
            PaymentApiResponse response = executor.execute();
            if (response == null || response.payload() == null) {
                throw new CustomException(errorCode);
            }
            return response.payload();
        } catch (CustomException e) {
            throw e;
        } catch (RestClientException e) {
            throw new CustomException(errorCode);
        }
    }

    private String bearerToken() {
        return "Bearer " + paymentProperties.getApiSecretKey();
    }

    @FunctionalInterface
    private interface PaymentRequestExecutor {
        PaymentApiResponse execute();
    }
}
