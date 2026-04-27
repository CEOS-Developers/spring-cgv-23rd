package com.ceos23.spring_boot.infra.payment;

import com.ceos23.spring_boot.infra.payment.client.PaymentFeignClient;
import com.ceos23.spring_boot.infra.payment.dto.PaymentAuthData;
import com.ceos23.spring_boot.infra.payment.dto.PaymentData;
import com.ceos23.spring_boot.infra.payment.dto.PaymentInstantRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewayImpl implements PaymentGateway {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CURRENCY_KRW = "KRW";

    private final PaymentFeignClient paymentFeignClient;
    private final PaymentProperties paymentProperties;

    private String cachedApiSecretKey;

    @Override
    public PaymentData pay(String paymentId, String orderName, Integer totalPayAmount, String customData) {
        PaymentInstantRequest request = new PaymentInstantRequest(
                paymentProperties.getStoreId(),
                orderName,
                totalPayAmount,
                CURRENCY_KRW,
                customData
        );

        return paymentFeignClient.instantPayment(
                authorization(),
                paymentId,
                request
        ).getData();
    }

    @Override
    public PaymentData cancel(String paymentId) {
        return paymentFeignClient.cancelPayment(
                authorization(),
                paymentId
        ).getData();
    }

    @Override
    public PaymentData getPayment(String paymentId) {
        return paymentFeignClient.getPayment(
                authorization(),
                paymentId
        ).getData();
    }

    private String authorization() {
        return BEARER_PREFIX + getApiSecretKey();
    }

    private String getApiSecretKey() {
        if (cachedApiSecretKey == null) {
            PaymentAuthData authData = paymentFeignClient
                    .getApiSecret(paymentProperties.getGithubId())
                    .getData();

            cachedApiSecretKey = authData.getApiSecretKey();
        }

        return cachedApiSecretKey;
    }
}