package com.ceos.spring_cgv_23rd.domain.payment.application.service;

import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ceos.spring_cgv_23rd.domain.payment.application.dto.result.PgPaymentResult;
import com.ceos.spring_cgv_23rd.domain.payment.application.port.out.PaymentPersistencePort;
import com.ceos.spring_cgv_23rd.domain.payment.domain.Payment;
import com.ceos.spring_cgv_23rd.domain.payment.domain.PaymentStatus;
import com.ceos.spring_cgv_23rd.domain.payment.exception.PaymentErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentTxService {

	private final PaymentPersistencePort paymentPersistencePort;

	@Transactional
	public Payment createReadyPayment(String paymentId, String orderName, int amount) {
		Payment payment = Payment.createReadyPayment(paymentId, orderName, amount);
		log.debug("READY payment created in DB");
		return paymentPersistencePort.save(payment);
	}

	@Transactional
	public Payment markPaid(String paymentId, PgPaymentResult pgResult) {
		// 결제 조회
		Payment payment = paymentPersistencePort.findByPaymentId(paymentId)
			.orElseThrow(() -> {
				log.error("Payment not found in markPaid");
				return new GeneralException(PaymentErrorCode.PAYMENT_NOT_FOUND);
			});

		// 결제 완료 상태 업데이트
		payment.markPaid(pgResult.pgProvider(), pgResult.paidAt());

		// 결제 완료 상태 DB 반영
		paymentPersistencePort.update(payment);

		log.info("Payment marked PAID in DB. pgProvider={}", pgResult.pgProvider());

		return payment;
	}

	@Transactional
	public void transitionStatus(String paymentId, Consumer<Payment> statusAction) {
		// 결제 조회
		Payment payment = paymentPersistencePort.findByPaymentId(paymentId)
			.orElseThrow(() -> {
				log.error("Payment not found in transitionStatus");
				return new GeneralException(PaymentErrorCode.PAYMENT_NOT_FOUND);
			});

		PaymentStatus oldStatus = payment.getStatus();

		// 결제 상태 업데이트 실행
		statusAction.accept(payment);

		// 전환된 상태 DB 반영
		paymentPersistencePort.update(payment);

		log.info("Payment status transitioned. from={}, to={}", oldStatus, payment.getStatus());
	}
}
