package com.cgv.spring_boot.domain.reservation.scheduler;

import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.domain.reservation.entity.ReservationStatus;
import com.cgv.spring_boot.domain.reservation.repository.ReservationRepository;
import com.cgv.spring_boot.domain.reservation.repository.ReservedSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservedSeatRepository reservedSeatRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingReservations() {
        List<Reservation> expiredReservations = reservationRepository.findAllByStatusAndExpiresAtBefore(
                ReservationStatus.PENDING_PAYMENT,
                LocalDateTime.now()
        );

        for (Reservation reservation : expiredReservations) {
            reservation.expire();
            reservedSeatRepository.deleteByReservation(reservation);
        }
    }
}
