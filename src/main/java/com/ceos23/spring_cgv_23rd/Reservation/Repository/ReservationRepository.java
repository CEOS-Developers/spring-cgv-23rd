package com.ceos23.spring_cgv_23rd.Reservation.Repository;

import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationSeat;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
        select r from Reservation r
        where r.status = 'RESERVED'
    """)
    List<Reservation> findByScreening(Screening screening);

    @Query("""
        select r from Reservation r
        where r.id = :targetId
        and r.status = 'RESERVED'
    """)
    Optional<Reservation> findActivatedReservationById(@Param("targetId") long id);

    @Query("""
        select r from Reservation r
        where r.expired = false
        and r.expiredAt <= :now
    """)
    List<Reservation> findExpired(@Param("now") LocalDateTime now);

}
