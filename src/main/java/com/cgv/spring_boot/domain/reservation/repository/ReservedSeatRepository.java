package com.cgv.spring_boot.domain.reservation.repository;

import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.domain.reservation.entity.ReservedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
    @Query("SELECT COUNT(rs) > 0 FROM ReservedSeat rs " +
            "WHERE rs.reservation.schedule.id = :scheduleId " +
            "AND rs.seatRow = :row " +
            "AND rs.seatCol = :col")
    boolean existsByScheduleAndRowAndCol(
            @Param("scheduleId") Long scheduleId,
            @Param("row") String row,
            @Param("col") int col
    );

    void deleteByReservation(Reservation reservation);
}
