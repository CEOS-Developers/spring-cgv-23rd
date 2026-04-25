package com.ceos.spring_boot.domain.payment.service;

import com.ceos.spring_boot.domain.payment.client.PaymentClient;
import com.ceos.spring_boot.domain.payment.dto.PaymentRequest;
import com.ceos.spring_boot.domain.payment.dto.PaymentResponse;
import com.ceos.spring_boot.domain.payment.entity.Payment;
import com.ceos.spring_boot.domain.payment.entity.PaymentCategory;
import com.ceos.spring_boot.domain.payment.entity.PaymentStatus;
import com.ceos.spring_boot.domain.payment.repository.PaymentRepository;
import com.ceos.spring_boot.domain.reservation.entity.Reservation;
import com.ceos.spring_boot.domain.reservation.repository.ReservationRepository;
import com.ceos.spring_boot.domain.store.entity.Order;
import com.ceos.spring_boot.domain.store.repository.OrderRepository;
import com.ceos.spring_boot.domain.store.service.StoreService;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final StoreService storeService;

    // 결제 생성 및 요청
    @Transactional
    public PaymentResponse createPayment(Long userId, PaymentRequest request, PaymentCategory category, Long targetId) {

        // 내부 결제 로그 저장
        String paymentId = java.util.UUID.randomUUID().toString().replace("-", "");

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .amount(request.totalPayAmount())
                .status(PaymentStatus.READY)
                .category(category)
                .targetId(targetId)
                .build();
        paymentRepository.save(payment);

        try {
            // 외부 결제 API 호출
            PaymentResponse response = paymentClient.requestPayment(paymentId, request);

            // 카테고리별 도메인 상태 확정
            if (category == PaymentCategory.MOVIE) {
                Reservation reservation = reservationRepository.findById(targetId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND_ERROR));
                reservation.confirm(); // PAYMENT_PENDING -> CONFIRMED
            } else if (category == PaymentCategory.STORE) {
                Order order = orderRepository.findById(targetId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND_ERROR));
                order.complete(); // PENDING -> COMPLETED
            }

            payment.markAsPaid();
            return response;

        } catch (Exception e) {
            log.error("[Payment Failed] 보상 트랜잭션 진행 예정 - 결제 ID: {}", paymentId);
            payment.markAsFailed();

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
    @Transactional
    public PaymentResponse cancelPayment(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        // 결제 로그 상태 변경
        payment.cancel();

        // 외부 API 취소 요청
        PaymentResponse response = paymentClient.cancelPayment(paymentId);

        // 도메인 복구
        if (payment.getCategory() == PaymentCategory.MOVIE) {
            Reservation reservation = reservationRepository.findById(payment.getTargetId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND_ERROR));
            reservation.cancel(); // CONFIRMED -> CANCELED
        } else if (payment.getCategory() == PaymentCategory.STORE) {
            storeService.restoreStock(payment.getTargetId()); // 재고 복구
        }

        return response;
    }
}