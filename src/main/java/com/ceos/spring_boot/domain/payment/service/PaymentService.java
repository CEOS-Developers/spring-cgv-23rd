package com.ceos.spring_boot.domain.payment.service;

import com.ceos.spring_boot.domain.payment.client.PaymentClient;
import com.ceos.spring_boot.domain.payment.dto.PaymentRequest;
import com.ceos.spring_boot.domain.payment.dto.PaymentResponse;
import com.ceos.spring_boot.domain.payment.entity.Payment;
import com.ceos.spring_boot.domain.payment.entity.PaymentCategory;
import com.ceos.spring_boot.domain.payment.entity.PaymentStatus;
import com.ceos.spring_boot.domain.payment.repository.PaymentRepository;
import com.ceos.spring_boot.domain.payment.strategy.PaymentStrategy;
import com.ceos.spring_boot.domain.reservation.entity.Reservation;
import com.ceos.spring_boot.domain.reservation.repository.ReservationRepository;
import com.ceos.spring_boot.domain.reservation.service.ReservationService;
import com.ceos.spring_boot.domain.store.entity.Order;
import com.ceos.spring_boot.domain.store.repository.OrderRepository;
import com.ceos.spring_boot.domain.store.service.StoreService;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;
    private final TransactionTemplate transactionTemplate;

    private final List<PaymentStrategy> paymentStrategies;

    // 결제 생성 및 요청
    public PaymentResponse createPayment(Long userId, PaymentRequest request, PaymentCategory category, Long targetId) {

        PaymentStrategy strategy = paymentStrategies.stream()
                .filter(s -> s.supports(category))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 결제 카테고리입니다."));

        // 내부 결제 로그 저장
        String paymentId = java.util.UUID.randomUUID().toString().replace("-", "");

        Payment payment = transactionTemplate.execute(status -> {
            Payment p = Payment.createPayment(paymentId, request.totalPayAmount(), targetId, category);
            return paymentRepository.save(p);
        });

        boolean isPaymentProcessed = false; // 외부 결제 API 호출 성공 여부 확인용

        try {
            // 외부 결제 API 호출
            PaymentResponse response = paymentClient.requestPayment(paymentId, request);
            isPaymentProcessed = true;

            // 카테고리별 도메인 상태 확정
            transactionTemplate.executeWithoutResult(status -> {
                strategy.confirm(targetId);

                Payment p = paymentRepository.findByPaymentId(paymentId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

                p.markAsPaid();
            });
            return response;

        } catch (Exception e) {
            log.error("[Payment Failed] 보상 트랜잭션 진행 - ID: {}, 원인: {}", paymentId, e.getMessage(), e);

            transactionTemplate.executeWithoutResult(status -> {
                Payment p = paymentRepository.findByPaymentId(paymentId).orElse(null);
                if (p != null) p.markAsFailed();
            });

            strategy.compensate(targetId, paymentId, isPaymentProcessed);

            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }
    }

    // 결제 내역 조회
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentDetail(String paymentId) {
        paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        return paymentClient.getPaymentDetail(paymentId);
    }

    // 결제 취소
    public PaymentResponse cancelPayment(String paymentId) {

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_CANCELLABLE);
        }

        // 외부 API 취소 요청
        PaymentResponse response = paymentClient.cancelPayment(paymentId);

        // DB 반영 및 도메인 로직 처리
        try {
            transactionTemplate.executeWithoutResult(status -> {
                // 트랜잭션 내에서 다시 조회하여 변경 감지(Dirty Checking)로 상태 업데이트
                Payment p = paymentRepository.findByPaymentId(paymentId).orElseThrow();
                p.cancel();

                PaymentStrategy strategy = paymentStrategies.stream()
                        .filter(s -> s.supports(p.getCategory()))
                        .findFirst()
                        .orElseThrow();

                strategy.cancel(p.getTargetId());
            });
        } catch (Exception e) {
            log.error("[CRITICAL] 결제 취소 API는 성공했으나 DB 반영에 실패했습니다. 결제 ID: {}, 원인: {}",
                    paymentId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.PAYMENT_ROLLBACK_FAILED);
        }

        return response;
    }
}