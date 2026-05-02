package com.ceos.spring_boot.domain.payment.strategy;

import com.ceos.spring_boot.domain.payment.entity.PaymentCategory;

public interface PaymentStrategy {
    // 어떤 카테고리(MOVIE, STORE)를 담당할지 판별하는 메서드
    boolean supports(PaymentCategory category);

    // 결제 성공 시 도메인(영화, 매점)의 상태를 확정하는 메서드
    void confirm(Long targetId);

    // 결제 실패 시 도메인에 맞는 롤백을 수행하는 메서드
    void compensate(Long targetId, String paymentId, boolean isPaymentProcessed);

    void cancel(Long targetId);
}
