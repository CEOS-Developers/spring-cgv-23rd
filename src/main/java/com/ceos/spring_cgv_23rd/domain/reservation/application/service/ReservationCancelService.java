package com.ceos.spring_cgv_23rd.domain.reservation.application.service;

import com.ceos.spring_cgv_23rd.domain.payment.application.port.in.CancelPaymentUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.in.CancelReservationUseCase;
import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.ReservationPersistencePort;
import com.ceos.spring_cgv_23rd.domain.reservation.domain.Reservation;
import com.ceos.spring_cgv_23rd.domain.reservation.exception.ReservationErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationCancelService implements CancelReservationUseCase {

    private final ReservationPersistencePort reservationPersistencePort;
    private final ReservationTxService reservationTxService;
    private final CancelPaymentUseCase cancelPaymentUseCase;


    @Override
    public void cancel(Long userId, Long reservationId) {
        // 예매 정보 조회
        Reservation reservation = reservationPersistencePort.findReservationWithSeatsById(reservationId)
                .orElseThrow(() -> new GeneralException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 예매한 사용자인지 검증
        if (reservation.isGuest() || !userId.equals(reservation.getUserId())) {
            throw new GeneralException(ReservationErrorCode.RESERVATION_FORBIDDEN);
        }

        // 외부 PG사 취소
        cancelPaymentUseCase.cancel(reservation.getPaymentId());

        // 예매 취소 상태 및 잔여 좌석 수 복구
        try {
            reservationTxService.applyCancellation(reservation);
        } catch (Exception e) {
            log.error("결제는 취소되었으나 예매 상태/좌석 복구 실패. reservationId={}, paymentId={}", reservationId, reservation.getPaymentId(), e);
            throw new GeneralException(ReservationErrorCode.CONFIRM_FAILED_ROLLED_BACK);
        }
    }
}
