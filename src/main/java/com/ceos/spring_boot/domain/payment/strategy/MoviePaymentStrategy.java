package com.ceos.spring_boot.domain.payment.strategy;

import com.ceos.spring_boot.domain.payment.entity.PaymentCategory;
import com.ceos.spring_boot.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoviePaymentStrategy implements PaymentStrategy {

    private final ReservationService reservationService;

    @Override
    public boolean supports(PaymentCategory category) {
        return category == PaymentCategory.MOVIE;
    }

    @Override
    public void confirm(Long targetId) {
        reservationService.confirmReservation(targetId);
    }

    @Override
    public void compensate(Long targetId, String paymentId, boolean isPaymentProcessed) {
        // 결제 중 에러 나면 좌석 선점 해제
        reservationService.rollbackPreOccupiedReservation(targetId);
    }

    @Override
    public void cancel(Long targetId) {
        // 사용자가 취소하면 예약 상태를 CANCELED로 변경하고 좌석 해제
        reservationService.cancelReservation(targetId);
    }
}
