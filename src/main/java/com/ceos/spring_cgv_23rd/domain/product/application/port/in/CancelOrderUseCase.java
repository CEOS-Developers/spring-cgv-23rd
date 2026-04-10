package com.ceos.spring_cgv_23rd.domain.product.application.port.in;

public interface CancelOrderUseCase {
    void cancelOrder(Long userId, Long orderId);
}
