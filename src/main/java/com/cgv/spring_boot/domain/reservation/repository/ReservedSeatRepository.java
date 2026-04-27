package com.cgv.spring_boot.domain.reservation.repository;

import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.domain.reservation.entity.ReservedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
    boolean existsByScheduleIdAndSeatRowAndSeatCol(Long scheduleId, String seatRow, int seatCol);

    long countByReservationId(Long reservationId);

    void deleteByReservation(Reservation reservation);
}
