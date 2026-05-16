package com.cgv.spring_boot.domain.payment.service;

import com.cgv.spring_boot.domain.payment.dto.request.PaymentCreateRequest;
import com.cgv.spring_boot.domain.payment.dto.response.PaymentResponse;
import com.cgv.spring_boot.domain.payment.entity.Payment;
import com.cgv.spring_boot.domain.payment.entity.PaymentStatus;
import com.cgv.spring_boot.domain.payment.exception.PaymentErrorCode;
import com.cgv.spring_boot.domain.payment.repository.PaymentRepository;
import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String DEFAULT_CURRENCY = "KRW";

    private final PaymentRepository paymentRepository;
    private final PortOnePaymentClient portOnePaymentClient;
    private final PaymentIdGenerator paymentIdGenerator;

    @Transactional
    public PaymentResponse payReservation(Reservation reservation, int totalAmount, String orderName, String customData) {
        if (paymentRepository.existsByReservationId(reservation.getId())) {
            log.warn("payment rejected. reservationId={}, reason=payment_already_exists", reservation.getId());
            throw new BusinessException(PaymentErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        String paymentId = paymentIdGenerator.generate();
        log.info("payment requested. reservationId={}, paymentId={}, totalAmount={}",
                reservation.getId(), paymentId, totalAmount);
        Payment payment = paymentRepository.save(
                Payment.createReady(reservation, paymentId, orderName, totalAmount, DEFAULT_CURRENCY, customData)
        );

        try {
            PaymentResponse response = portOnePaymentClient.instantPay(paymentId, new PaymentCreateRequest(
                    orderName,
                    totalAmount,
                    DEFAULT_CURRENCY,
                    customData
            ));
            payment.markPaid(response.pgProvider(), response.paidAt());
            log.info("AUDIT payment succeeded. reservationId={}, paymentId={}, provider={}",
                    reservation.getId(), response.paymentId(), response.pgProvider());
            return response;
        } catch (BusinessException e) {
            payment.markFailed();
            log.warn("AUDIT payment failed. reservationId={}, paymentId={}, reason={}",
                    reservation.getId(), paymentId, e.getErrorCode().getMessage());
            throw e;
        }
    }

    @Transactional
    public void cancelReservationPayment(Reservation reservation) {
        Payment payment = paymentRepository.findByReservationId(reservation.getId())
                .orElse(null);

        if (payment == null || payment.getStatus() != PaymentStatus.PAID) {
            return;
        }

        portOnePaymentClient.cancel(payment.getPaymentId());
        payment.cancel();
        log.info("AUDIT payment cancelled. reservationId={}, paymentId={}",
                reservation.getId(), payment.getPaymentId());
    }
}
