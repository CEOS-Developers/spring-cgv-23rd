package com.ceos23.spring_boot.domain.payment.client;

import com.ceos23.spring_boot.domain.payment.client.dto.ApiResponse;
import com.ceos23.spring_boot.domain.payment.client.dto.PaymentData;
import com.ceos23.spring_boot.domain.payment.client.dto.PaymentRequest;
import com.ceos23.spring_boot.domain.payment.client.dto.PaymentTokenData;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestClient restClient;
    private final StringRedisTemplate redisTemplate;

    private static final String SECRET_KEY_PREFIX = "apiSecretKey:";
    private static final String BEARER = "Bearer ";
    private static final String GITHUBID = "take21";

    public PaymentData requestInstantPayment(String paymentId, PaymentRequest requestBody) {
        String apiSecretKey = getSecretKey(GITHUBID);

        return executeApi(
                ()-> restClient.post()
                        .uri("/payments/{paymentId}/instant", paymentId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + apiSecretKey)
                        .body(requestBody),
                new ParameterizedTypeReference<>(){}
        );
    }

    public PaymentData cancelPayment(String paymentId) {
        String apiSecretKey = getSecretKey(GITHUBID);

        return executeApi(
                () -> restClient.post()
                        .uri("/payments/{paymentId}/cancel", paymentId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + apiSecretKey),
                new ParameterizedTypeReference<>() {}
        );
    }

    public PaymentData getPaymentDetails(String paymentId) {
        String apiSecretKey = getSecretKey(GITHUBID);

        return executeApi(
                () -> restClient.get()
                        .uri("/payments/{paymentId}", paymentId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + apiSecretKey),
                new ParameterizedTypeReference<>() {}
        );
    }

    private String getSecretKey(String githubId) {
        String redisKey = SECRET_KEY_PREFIX + githubId;

        try {
            String cachedKey = redisTemplate.opsForValue().get(redisKey);

            if (cachedKey != null)
                return cachedKey;
        } catch (Exception e) {
            log.error("Redis와 통신 오류 발생 (API 호출로 우회)", e);
        }

        String newKey = getApiSecretKey(githubId);

        try {
            redisTemplate.opsForValue().set(
                    redisKey,
                    newKey,
                    Duration.ofHours(24)
            );
        } catch (Exception e) {
            log.error("Redis에 Secret Key 저장 실패", e);
        }

        return newKey;
    }

    private String getApiSecretKey(String githubId) {
        ApiResponse<PaymentTokenData> response = restClient.get()
                .uri("/auth/{githubId}", githubId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("Secret Key 발급 4xx 에러: {}", new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8));
                    throw new BusinessException(ErrorCode.EXTERNAL_API_CLIENT_ERROR);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("Secret Key 발급 5xx 에러: {}", new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8));
                    throw new BusinessException(ErrorCode.EXTERNAL_API_SERVER_ERROR);
                })
                .body(new ParameterizedTypeReference<>() {});

        validateResponse(response);
        return response.data().apiSecretKey();
    }

    private <T> T executeApi(Supplier<RestClient.RequestHeadersSpec<?>> supplier,
                             ParameterizedTypeReference<ApiResponse<T>> typeReference) {

        ApiResponse<T> response = supplier.get()
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("외부 API 클라이언트 에러 발생: {}", res.getStatusCode());
                    throw new BusinessException(ErrorCode.EXTERNAL_API_CLIENT_ERROR);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("외부 API 서버 에러 발생: {}", res.getStatusCode());
                    throw new BusinessException(ErrorCode.EXTERNAL_API_SERVER_ERROR);
                })
                .body(typeReference);

        validateResponse(response);
        return response.data();
    }

    private <T> void validateResponse(ApiResponse<T> response) {
        log.info("외부 API 응답 수신 - response 객체: {}", response);

        if (response == null) {
            log.error("응답 객체 자체가 null입니다.");
            throw new BusinessException(ErrorCode.INVALID_API_RESPONSE);
        }

        if (response.data() == null) {
            log.error("응답은 성공(200)했으나 data가 null입니다. API 응답 코드: {}, 메시지: {}", response.code(), response.message());
            throw new BusinessException(ErrorCode.INVALID_API_RESPONSE);
        }
    }
}
