package com.ceos23.cgv_clone.reservation.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.repository.LockRepository;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.reservation.dto.request.ReservationRequest;
import com.ceos23.cgv_clone.reservation.dto.response.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service("reservationServiceNamed")
@RequiredArgsConstructor
public class ReservationServiceNamed implements ReservationService{

    private static final int LOCK_TIMEOUT_SECONDS = 3;

    private final LockRepository lockRepository;
    private final ReservationServiceNamedInner inner;

    @Override
    public ReservationResponse createReservation(Long userId, ReservationRequest request) {
        String key = "schedule:" + request.getScheduleId();

        Connection lockConn = lockRepository.acquireLock(key, LOCK_TIMEOUT_SECONDS);
        if (lockConn == null) {
            throw new CustomException(ErrorCode.CONCURRENT_UPDATE_FAILED);
        }

        try {
            return inner.createReservation(userId, request);
        } finally {
            lockRepository.releaseLock(lockConn, key);
        }
    }

    @Override
    public void cancelReservation(Long userId, Long reservationId) {
        inner.cancelReservation(userId, reservationId);
    }
}
