package com.ceos23.cgv_clone.reservation.scheduler;

import com.ceos23.cgv_clone.reservation.service.ReservationTimeoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationTimeoutScheduler {

    private final ReservationTimeoutService reservationTimeoutService;

    @Scheduled(fixedRate = 60_000)
    public void run() {
        reservationTimeoutService.cancelExpiredPending();
    }
}
