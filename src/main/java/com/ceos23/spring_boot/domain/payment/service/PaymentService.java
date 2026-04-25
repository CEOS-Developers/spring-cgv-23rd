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
    private final ReservationLockFacade reservationLockFacade;
    private final PaymentClient paymentClient;
    private final ReservationService reservationService;

    private static final String STOREID = "take21";
    private static final String CURRENCY = "KRW";

    public PaymentDataInfo requestInstantPayment(ReservationCreateCommand command, FrontendPaymentRequest request) {
        ReservationInfo info = reservationLockFacade.createReservationWithLock(command);
        String paymentId = info.paymentId();

        if (!info.totalPrice().equals(request.totalPayAmount())) {
            reservationService.cancelReservation(paymentId);
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        PaymentRequest paymentRequest = new PaymentRequest(
                STOREID,
                info.orderName(),
                info.totalPrice(),
                CURRENCY,
                ""
        );

        try {
            PaymentData response = paymentClient.requestInstantPayment(paymentId, paymentRequest);

            if (!"PAID".equals(response.paymentStatus()))
                throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);

            reservationService.confirmPayment(paymentId);
            return PaymentDataInfo.from(response);

        } catch (BusinessException be) {
            reservationService.cancelReservation(paymentId);
            throw be;

        }catch (Exception e) {
            reservationService.cancelReservation(paymentId);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void cancelPayment(String paymentId) {
        reservationService.verifyCancelable(paymentId);

        PaymentData response = paymentClient.cancelPayment(paymentId);

        if (!"CANCELLED".equals(response.paymentStatus()))
            throw new BusinessException(ErrorCode.PAYMENT_CANCEL_FAILED);

        reservationService.cancelReservation(paymentId);
    }

    public PaymentDataInfo getPaymentDetails(String paymentId) {
        PaymentData response = paymentClient.getPaymentDetails(paymentId);

        return PaymentDataInfo.from(response);
    }
}
