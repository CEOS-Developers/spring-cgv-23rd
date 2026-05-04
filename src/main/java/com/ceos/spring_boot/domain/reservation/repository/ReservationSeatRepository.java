package com.ceos.spring_boot.domain.reservation.repository;

import com.ceos.spring_boot.domain.cinema.entity.ScreenType;
import com.ceos.spring_boot.domain.cinema.entity.Seat;
import com.ceos.spring_boot.domain.reservation.entity.ReservationSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.screenType = :screenType " +
            "AND CONCAT(UPPER(s.seatRow), '-', s.seatCol) IN :identifiers")
    List<Seat> findByScreenTypeAndSeatIdentifiers(
            @Param("screenType") ScreenType screenType,
            @Param("identifiers") List<String> identifiers
    );

    // 특정 상영 일정에 요청된 좌석 ID 중 하나라도 이미 예약된 것이 있는지 한 번에 검증
    boolean existsByScheduleIdAndSeatIdIn(Long scheduleId, List<Long> seatIds);

    // 취소 시 사용되는 삭제 메서드
    void deleteByReservationId(Long reservationId);

}
