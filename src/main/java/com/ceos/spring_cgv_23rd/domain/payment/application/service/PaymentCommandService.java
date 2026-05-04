package com.ceos.spring_cgv_23rd.domain.payment.application.service;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.command.PayCommand;
import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PaymentResult;
import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PgPaymentResult;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.CancelPaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.PaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.out.PaymentPersistencePort;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.out.PgClientPort;
import com.ceos.spring_cgv_23rd.domain.payment.domain.Payment;
import com.ceos.spring_cgv_23rd.domain.payment.exception.PaymentErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentUseCase, CancelPaymentUseCase {

    private final PaymentTxService txService;
    private final PaymentPersistencePort paymentPersistencePort;
    private final PgClientPort pgClient;

    @Override
    public PaymentResult pay(PayCommand command) {
        String paymentId = command.paymentId();

        // 중복 결제 검증 (멱등성)
        Optional<Payment> existing = paymentPersistencePort.findByPaymentId(paymentId);
        if (existing.isPresent()) {
            return handleDuplicate(existing.get());
        }

        // 결제 대기(READY) 상태 임시 저장
        Payment payment = txService.createReadyPayment(paymentId, command.orderName(), command.amount());

        // 외부 PG사 연동 및 결제 반영
        try {
            PgPaymentResult pgResult = callPgForPay(payment);
            Payment paid = txService.markPaid(paymentId, pgResult);
            return PaymentResult.of(paid);
        } catch (Exception e) {
            log.warn("Payment failed. Trying compensation. paymentId: {}", paymentId, e);
            compensate(paymentId);
            throw new GeneralException(PaymentErrorCode.PG_CALL_FAILED);
        }
    }

    @Override
    public void cancel(String paymentId) {
        // 결제 조회
        Payment payment = paymentPersistencePort.findByPaymentId(paymentId)
                .orElseThrow(() -> new GeneralException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 결제가 이미 취소된 상태인지 검증
        if (payment.isCancelled()) {
            throw new GeneralException(PaymentErrorCode.PAYMENT_ALREADY_CANCELLED);
        }

        // 결제가 완료된 상태인지 검증
        if (!payment.isPaid()) {
            throw new GeneralException(PaymentErrorCode.CANNOT_CANCEL_NOT_PAID);
        }

        // 외부 PG사 결제 취소 API 호출
        try {
            pgClient.cancel(paymentId);
        } catch (Exception e) {
            log.warn("PG cancel failed. paymentId={}", paymentId, e);
            throw new GeneralException(PaymentErrorCode.PG_CANCEL_FAILED);
        }

        // DB 결제 기록을 CANCELLED 상태로 변경
        txService.transitionStatus(paymentId, Payment::markCancelled);
    }


    // 외부 PG사 결제 API 호출
    private PgPaymentResult callPgForPay(Payment payment) {
        PgPaymentResult pg;

        try {
            pg = pgClient.pay(payment);
        } catch (Exception e) {
            log.warn("PG call failed. paymentId={}", payment.getPaymentId(), e);
            throw new GeneralException(PaymentErrorCode.PG_CALL_FAILED);
        }

        if (!pg.paid()) {
            throw new GeneralException(PaymentErrorCode.PG_PAYMENT_FAILED);
        }

        return pg;
    }

    // 중복 결제 검증
    private PaymentResult handleDuplicate(Payment payment) {
        if (payment.isPaid())
            throw new GeneralException(PaymentErrorCode.PAYMENT_ALREADY_PAID);

        if (payment.isInProgress())
            throw new GeneralException(PaymentErrorCode.PAYMENT_IN_PROGRESS);

        throw new GeneralException(PaymentErrorCode.PAYMENT_ALREADY_FAILED);
    }

    // 실패시 외부 PG사 취소 API 호출
    private void compensate(String paymentId) {
        try {
            pgClient.cancel(paymentId);
        } catch (Exception e) {
            log.error("PG compensate cancel failed. paymentId={}", paymentId, e);
        }

        try {
            txService.transitionStatus(paymentId, Payment::markFailed);
        } catch (Exception e) {
            log.warn("markFailed failed. paymentId={}", paymentId, e);
        }
    }
}
