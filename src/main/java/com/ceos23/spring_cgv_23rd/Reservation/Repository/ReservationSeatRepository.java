package com.ceos23.spring_cgv_23rd.Reservation.Repository;

import com.ceos23.spring_cgv_23rd.Reservation.DTO.Request.ReservationType;
import com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservedSeatQuantityResponseDTO;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.Reservation;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationSeat;
import com.ceos23.spring_cgv_23rd.Reservation.Domain.ReservationStatus;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    @Query("""
        select rs from ReservationSeat rs
        where rs.screening = :screening
        and rs.reservationStatus = :status
    """)
    List<ReservationSeat> findByScreeningAndStatus(
            @Param("screening") Screening screening,
            @Param("status") ReservationStatus reservationStatus);

    @Query("""
        select new com.ceos23.spring_cgv_23rd.Reservation.DTO.Response.ReservedSeatQuantityResponseDTO(
            s.screening.id,
            count(s)
        )
        from ReservationSeat s
        where s.screening.id in :screeningIds
        and s.reservationStatus = :reservationStatus
        group by s.screening.id
    """)
    List<ReservedSeatQuantityResponseDTO> getQuantityOfReservedSeatByScreening(
            @Param("screeningIds") List<Long> screeningIds,
            @Param("reservationStatus") ReservationStatus reservationStatus
    );
}
