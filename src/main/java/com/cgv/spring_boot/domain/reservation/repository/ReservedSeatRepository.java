package com.cgv.spring_boot.domain.reservation.repository;

import com.cgv.spring_boot.domain.reservation.entity.Reservation;
import com.cgv.spring_boot.domain.reservation.entity.ReservedSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rs FROM ReservedSeat rs " +
            "WHERE rs.schedule.id = :scheduleId " +
            "AND rs.seatRow IN :rows " +
            "AND rs.seatCol IN :cols")
    List<ReservedSeat> findAllByScheduleAndRowsAndCols(
            @Param("scheduleId") Long scheduleId,
            @Param("rows") List<String> rows,
            @Param("cols") List<Integer> cols
    );

    void deleteByReservation(Reservation reservation);
}
