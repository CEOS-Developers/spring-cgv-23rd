package com.ceos.spring_cgv_23rd.domain.product.application.service;

import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.CancelPaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.product.application.port.in.CancelOrderUseCase;
import com.ceos.spring_cgv_23rd.domain.product.application.port.out.ProductPersistencePort;
import com.ceos.spring_cgv_23rd.domain.product.domain.ProductOrder;
import com.ceos.spring_cgv_23rd.domain.product.exception.ProductErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCancelService implements CancelOrderUseCase {

    private final ProductPersistencePort productPersistencePort;
    private final CancelPaymentUseCase cancelPaymentUseCase;
    private final ProductTxService productTxService;

    @Override
    public void cancelOrder(Long userId, Long orderId) {

        // 주문 조회
        ProductOrder order = productPersistencePort.findOrderWithItemsById(orderId)
                .orElseThrow(() -> new GeneralException(ProductErrorCode.ORDER_NOT_FOUND));

        // 본인 주문인지 확인
        if (!userId.equals(order.getUserId())) {
            throw new GeneralException(ProductErrorCode.ORDER_FORBIDDEN);
        }

        // 외부 PG사 취소
        cancelPaymentUseCase.cancel(order.getPaymentId());

        // 주문 취소 상태 및 재고 복구
        try {
            productTxService.applyCancellation(order);
        } catch (Exception e) {
            log.error("결제는 취소되었으나 주문 상태/재고 복구 실패. orderId={}, paymentId={}", orderId, order.getPaymentId(), e);
            throw new GeneralException(ProductErrorCode.CONFIRM_FAILED_ROLLED_BACK);
        }
    }
}
