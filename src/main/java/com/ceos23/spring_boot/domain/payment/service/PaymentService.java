package com.ceos23.spring_boot.domain.payment.service;

import com.ceos23.spring_boot.domain.payment.client.PaymentClient;
import com.ceos23.spring_boot.domain.payment.client.dto.PaymentData;
import com.ceos23.spring_boot.domain.payment.client.dto.PaymentRequest;
import com.ceos23.spring_boot.domain.payment.dto.FrontendPaymentRequest;
import com.ceos23.spring_boot.domain.payment.dto.PaymentDataInfo;
import com.ceos23.spring_boot.domain.reservation.dto.ReservationCreateCommand;
import com.ceos23.spring_boot.domain.reservation.dto.ReservationInfo;
import com.ceos23.spring_boot.domain.reservation.entity.Reservation;
import com.ceos23.spring_boot.domain.reservation.facade.ReservationLockFacade;
import com.ceos23.spring_boot.domain.reservation.repository.ReservationRepository;
import com.ceos23.spring_boot.domain.reservation.service.ReservationService;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentClient paymentClient;

    private static final String STOREID = "take21";
    private static final String CURRENCY = "KRW";

    public PaymentDataInfo requestInstantPayment(String paymentId, String orderName, Integer totalPrice) {
        PaymentRequest paymentRequest = new PaymentRequest(
                STOREID,
                orderName,
                totalPrice,
                CURRENCY,
                ""
        );

        PaymentData response;

        try {
            response = paymentClient.requestInstantPayment(paymentId, paymentRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (!"PAID".equals(response.paymentStatus())) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
        }

        return PaymentDataInfo.from(response);
    }

    public void cancelPayment(String paymentId) {
        PaymentData response = paymentClient.cancelPayment(paymentId);

        if (!"CANCELLED".equals(response.paymentStatus()))
            throw new BusinessException(ErrorCode.PAYMENT_CANCEL_FAILED);
    }

    public PaymentDataInfo getPaymentDetails(String paymentId) {
        PaymentData response = paymentClient.getPaymentDetails(paymentId);

        return PaymentDataInfo.from(response);
    }
}
