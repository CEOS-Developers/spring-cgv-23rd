package com.ceos.spring_boot.domain.payment.strategy;

import com.ceos.spring_boot.domain.payment.client.PaymentClient;
import com.ceos.spring_boot.domain.payment.entity.PaymentCategory;
import com.ceos.spring_boot.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorePaymentStrategy implements PaymentStrategy {

    private final StoreService storeService;
    private final PaymentClient paymentClient; // 환불을 위해 주입

    @Override
    public boolean supports(PaymentCategory category) {
        return category == PaymentCategory.STORE;
    }

    @Override
    public void confirm(Long targetId) {
        storeService.completeOrder(targetId);
    }

    @Override
    public void compensate(Long targetId, String paymentId, boolean isPaymentProcessed) {
        if (isPaymentProcessed) {
            paymentClient.cancelPayment(paymentId);
        }
    }

    @Override
    public void cancel(Long targetId) {
        storeService.restoreStock(targetId);
    }
}
