package com.ceos23.spring_boot.cgv.repository.reservation;

import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationSeat;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    @Query("""
            select rs.seatTemplate.id
            from ReservationSeat rs
            where rs.screening = :screening
              and rs.seatTemplate in :seatTemplates
              and rs.reservation.status = :status
            """)
    List<Long> findReservedSeatTemplateIdsByScreeningAndSeatTemplates(
            Screening screening,
            List<SeatTemplate> seatTemplates,
            ReservationStatus status
    );

    List<ReservationSeat> findAllByReservation(Reservation reservation);

    boolean existsByScreeningAndSeatTemplate(Screening screening, SeatTemplate seatTemplate);
}
