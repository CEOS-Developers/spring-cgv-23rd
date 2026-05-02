package com.ceos23.spring_boot.cgv.repository.reservation;

import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationSeat;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    @Query("""
            select rs.seatTemplate.id
            from ReservationSeat rs
            where rs.screening = :screening
              and rs.seatTemplate in :seatTemplates
              and (
                    rs.reservation.status = :confirmedStatus
                    or (
                        rs.reservation.status = :pendingStatus
                        and rs.reservation.expiresAt > :now
                    )
              )
            """)
    List<Long> findActiveSeatTemplateIdsByScreeningAndSeatTemplates(
            Screening screening,
            List<SeatTemplate> seatTemplates,
            ReservationStatus confirmedStatus,
            ReservationStatus pendingStatus,
            LocalDateTime now
    );

    @Query("""
            select rs.seatTemplate.id
            from ReservationSeat rs
            where rs.screening = :screening
              and (
                    rs.reservation.status = :confirmedStatus
                    or (
                        rs.reservation.status = :pendingStatus
                        and rs.reservation.expiresAt > :now
                    )
              )
            """)
    List<Long> findActiveSeatTemplateIdsByScreening(
            Screening screening,
            ReservationStatus confirmedStatus,
            ReservationStatus pendingStatus,
            LocalDateTime now
    );

    List<ReservationSeat> findAllByReservation(Reservation reservation);

    boolean existsByScreeningAndSeatTemplate(Screening screening, SeatTemplate seatTemplate);

    void deleteAllByReservation(Reservation reservation);
}
