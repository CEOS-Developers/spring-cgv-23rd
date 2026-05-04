package com.ceos23.cgv_clone.reservation.service;

import com.ceos23.cgv_clone.reservation.entity.Reservation;
import com.ceos23.cgv_clone.reservation.entity.ReservationStatus;
import com.ceos23.cgv_clone.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationTimeoutService {

    private static final int PENDING_TIMEOUT_MINUTES = 5;

    private final ReservationRepository reservationRepository;

    @Transactional
    public void cancelExpiredPending() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(PENDING_TIMEOUT_MINUTES);

        List<Reservation> expired = reservationRepository.findAllByStatusAndReservedAtBefore(ReservationStatus.PENDING, threshold);

        if (expired.isEmpty()) {
            return;
        }

        log.info("만료된 PENDING 예매 {}건 취소", expired.size());
        expired.forEach(Reservation::cancelPending);
    }
}
