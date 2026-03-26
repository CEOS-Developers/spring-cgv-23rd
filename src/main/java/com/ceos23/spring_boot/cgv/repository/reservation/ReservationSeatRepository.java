package com.ceos23.spring_boot.cgv.repository.reservation;

import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationSeat;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    boolean existsByScreeningAndSeatTemplateAndReservation_Status(
            Screening screening,
            SeatTemplate seatTemplate,
            ReservationStatus status
    );

    List<ReservationSeat> findAllByReservation(Reservation reservation);
}