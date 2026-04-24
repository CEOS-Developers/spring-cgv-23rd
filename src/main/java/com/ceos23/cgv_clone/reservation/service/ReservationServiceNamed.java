package com.ceos23.cgv_clone.reservation.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.repository.LockRepository;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.payment.service.PaymentService;
import com.ceos23.cgv_clone.reservation.dto.request.ReservationRequest;
import com.ceos23.cgv_clone.reservation.dto.response.PendingReservationResponse;
import com.ceos23.cgv_clone.reservation.dto.response.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service("reservationServiceNamed")
@RequiredArgsConstructor
public class ReservationServiceNamed {

    private final PaymentService paymentService;
    private static final int LOCK_TIMEOUT_SECONDS = 3;

    private final LockRepository lockRepository;
    private final ReservationServiceNamedInner inner;

    public PendingReservationResponse prepareReservation(Long userId, ReservationRequest request) {
        String key = "schedule:" + request.getScheduleId();

        Connection lockConn = lockRepository.acquireLock(key, LOCK_TIMEOUT_SECONDS);
        if (lockConn == null) {
            throw new CustomException(ErrorCode.CONCURRENT_UPDATE_FAILED);
        }
        try {
            return inner.prepareReservation(userId, request);
        } finally{
            lockRepository.releaseLock(lockConn, key);
        }
    }

    public ReservationResponse confirmReservation(Long userId, Long reservationId) {
        PendingReservation pending = inner.loadForPayment(userId, reservationId);

        try {
            paymentService.pay(pending.paymentId(), pending.orderName(), pending.totalPrice());
        } catch (Exception e) {
            inner.cancelReservation(userId, pending.reservationId());
            throw e;
        }

        return inner.confirmReservation(pending.reservationId());
    }

    public void cancelReservation(Long userId, Long reservationId) {
        String paymentId = inner.cancelReservation(userId, reservationId);
        paymentService.cancel(paymentId);
    }
}
