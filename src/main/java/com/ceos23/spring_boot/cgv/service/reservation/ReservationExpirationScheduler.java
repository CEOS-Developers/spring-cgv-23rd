package com.ceos23.spring_boot.cgv.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationExpirationScheduler {

    private final ReservationService reservationService;

    @Scheduled(fixedDelay = 60000)
    public void expireOverdueReservations() {
        reservationService.expireOverdueReservations();
    }
}
