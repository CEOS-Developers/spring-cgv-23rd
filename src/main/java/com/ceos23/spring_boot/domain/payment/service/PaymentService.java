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
    private final ReservationRepository reservationRepository;

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

        PaymentData response;

        try {
            response = paymentClient.requestInstantPayment(paymentId, paymentRequest);
        } catch (Exception e) {
            reservationService.cancelReservation(paymentId);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (!"PAID".equals(response.paymentStatus())) {
            reservationService.cancelReservation(paymentId);
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
        }

        try {
            reservationService.confirmPayment(paymentId);
            return PaymentDataInfo.from(response);
        } catch (Exception e) {
            cancelPayment(paymentId, command.email());
            throw new BusinessException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }
    }

    public void cancelPayment(String paymentId, String email) {
        Reservation reservation = reservationRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }

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
