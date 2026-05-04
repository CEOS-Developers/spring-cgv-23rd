package com.ceos23.spring_cgv_23rd.Reservation.Service;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Reservation.Repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationScheduler {
    private final ReservationRepository reservationRepository;

    public ReservationScheduler(ReservationRepository reservationRepository){
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void expireReservations(){
        List<Reservation> reservations = reservationRepository.findExpired(LocalDateTime.now());

        for (Reservation r : reservations){
            r.expired();
        }
    }
}
