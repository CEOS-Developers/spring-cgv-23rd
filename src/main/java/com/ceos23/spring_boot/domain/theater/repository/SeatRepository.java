package com.ceos23.spring_boot.domain.theater.repository;

import com.ceos23.spring_boot.domain.theater.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "seatGrade")
    @Query("select s from Seat s where s.id in :seatIds AND s.screen.id = :screenId AND s.deletedAt IS NULL")
    List<Seat> findAllByIdAndScreenIdAndDeletedAtIsNullWithLock(
            @Param("seatIds") List<Long> seatIds,
            @Param("screenId") Long screenId
    );

    List<Seat> findAllByIdInAndScreenIdAndDeletedAtIsNull(List<Long> seatIds, Long screenId);
}
