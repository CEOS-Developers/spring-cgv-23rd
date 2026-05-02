package com.ceos23.spring_boot.domain.reservation.scheduler;

import com.ceos23.spring_boot.domain.payment.dto.PaymentDataInfo;
import com.ceos23.spring_boot.domain.payment.service.PaymentService;
import com.ceos23.spring_boot.domain.reservation.entity.Reservation;
import com.ceos23.spring_boot.domain.reservation.entity.ReservationStatus;
import com.ceos23.spring_boot.domain.reservation.repository.ReservationRepository;
import com.ceos23.spring_boot.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredReservations() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        List<Reservation> expiredReservations = reservationRepository.findByStatusAndCreatedAtBefore(
                ReservationStatus.PENDING,
                fiveMinutesAgo
        );

        for (Reservation reservation : expiredReservations) {
            String paymentId = reservation.getPaymentId();
            try {
                boolean isPaid = false;
                try {
                    PaymentDataInfo pgData = paymentService.getPaymentDetails(paymentId);
                    if ("PAID".equals(pgData.paymentStatus())) {
                        isPaid = true;
                    }
                } catch (Exception e) {
                    log.warn("결제 조회 실패. paymentId: {}", paymentId);
                }

                if (isPaid) {
                    paymentService.cancelPayment(paymentId);
                }

                reservationService.cancelReservation(paymentId);

            } catch (Exception e) {
                log.error("좌석 청소 중 에러 발생. paymentId: {}", paymentId, e);
            }
        }
    }
}
