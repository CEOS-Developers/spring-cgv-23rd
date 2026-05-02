package com.ceos23.spring_boot.infra.payment;

import com.ceos23.spring_boot.infra.payment.client.PaymentRestClient;
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

    private final PaymentRestClient paymentRestClient;
    private final PaymentProperties paymentProperties;

    private volatile String cachedApiSecretKey;

    @Override
    public PaymentData pay(String paymentId, String orderName, Integer totalPayAmount, String customData) {
        PaymentInstantRequest request = new PaymentInstantRequest(
                paymentProperties.getStoreId(),
                orderName,
                totalPayAmount,
                CURRENCY_KRW,
                customData
        );

        return paymentRestClient.instantPayment(
                authorization(),
                paymentId,
                request
        ).data();
    }

    @Override
    public PaymentData cancel(String paymentId) {
        return paymentRestClient.cancelPayment(
                authorization(),
                paymentId
        ).data();
    }

    @Override
    public PaymentData getPayment(String paymentId) {
        return paymentRestClient.getPayment(
                authorization(),
                paymentId
        ).data();
    }

    private String authorization() {
        return BEARER_PREFIX + getApiSecretKey();
    }

    private String getApiSecretKey() {
        if (cachedApiSecretKey == null) {
            synchronized (this) {
                if (cachedApiSecretKey == null) {
                    PaymentAuthData authData = paymentRestClient
                            .getApiSecret(paymentProperties.getGithubId())
                            .data();

                    cachedApiSecretKey = authData.apiSecretKey();
                }
            }
        }

        return cachedApiSecretKey;
    }
}