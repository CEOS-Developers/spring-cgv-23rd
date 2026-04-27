package com.ceos.spring_cgv_23rd.domain.payment.adapter.out.pg;

import com.ceos.spring_cgv_23rd.domain.payment.adapter.out.pg.dto.PgErrorResponse;
import com.ceos.spring_cgv_23rd.domain.payment.adapter.out.pg.dto.PgPaymentRequest;
import com.ceos.spring_cgv_23rd.domain.payment.adapter.out.pg.dto.PgPaymentResponse;
import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PgPaymentResult;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.out.PgClientPort;
import com.ceos.spring_cgv_23rd.domain.payment.domain.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class PgClientAdapter implements PgClientPort {

    private final RestClient pgRestClient;
    private final ObjectMapper objectMapper;

    @Value("${pg.store-id}")
    private String storeId;

    @Override
    public PgPaymentResult pay(Payment payment) {

        try {
            PgPaymentResponse response = pgRestClient.post()
                    .uri("/payments/{paymentId}/instant", payment.getPaymentId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PgPaymentRequest(
                            storeId,
                            payment.getOrderName(),
                            payment.getAmount(),
                            "KRW",
                            null))
                    .retrieve()
                    .body(PgPaymentResponse.class);

            if (response == null || response.payload() == null) {
                log.warn("PG success response but payload is null. paymentId: {}", payment.getPaymentId());
                return PgPaymentResult.fail("No payload in response");
            }

            boolean paid = "PAID".equalsIgnoreCase(response.payload().paymentStatus());
            if (!paid) {
                log.warn("PG payment not PAID. paymentId: {}, paymentStatus: {}", payment.getPaymentId(), response.payload().paymentStatus());
                return PgPaymentResult.fail("Unexpected paymentStatus: " + response.payload().paymentStatus());
            }

            return PgPaymentResult.success(
                    response.payload().pgProvider(),
                    response.payload().paidAt());

        } catch (RestClientResponseException e) {
            PgErrorResponse error = parseError(e.getResponseBodyAsString());
            log.warn("PG error response. paymentId={}, httpStatus={}, pgStatus={}, message={}",
                    payment.getPaymentId(), e.getStatusCode(),
                    error != null ? error.status() : null,
                    error != null ? error.message() : null);

            return PgPaymentResult.fail(
                    error != null ? error.message() : e.getMessage());
        }
    }

    @Override
    public void cancel(String paymentId) {
        try {
            pgRestClient.post()
                    .uri("/payments/{paymentId}/cancel", paymentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.warn("PG cancel failed. paymentId={}, httpStatus={}, body={}",
                    paymentId, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private PgErrorResponse parseError(String body) {
        try {
            return objectMapper.readValue(body, PgErrorResponse.class);
        } catch (Exception e) {
            log.warn("Failed to parse PG error body: {}", body, e);
            return null;
        }
    }
}
