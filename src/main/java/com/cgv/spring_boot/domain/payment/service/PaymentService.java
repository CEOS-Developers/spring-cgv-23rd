package com.cgv.spring_boot.domain.payment.service;

import com.cgv.spring_boot.domain.payment.dto.request.PaymentCreateRequest;
import com.cgv.spring_boot.domain.payment.dto.response.PaymentResponse;
import com.cgv.spring_boot.domain.payment.entity.Payment;
import com.cgv.spring_boot.domain.payment.exception.PaymentErrorCode;
import com.cgv.spring_boot.domain.payment.repository.PaymentRepository;
import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new BusinessException(PaymentErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        String paymentId = paymentIdGenerator.generate();
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
            return response;
        } catch (BusinessException e) {
            payment.markFailed();
            throw e;
        }
    }
}
