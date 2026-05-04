package com.ceos.spring_boot.domain.schedule.repository;

import com.ceos.spring_boot.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 상영관에서 시간대가 겹치는 스케줄이 있는지 확인
    @Query("SELECT s FROM Schedule s WHERE s.screen.id = :screenId " +
            "AND ((s.startAt < :endAt AND s.endAt > :startAt))")
    List<Schedule> findOverlappingSchedules(
            @Param("screenId") Long screenId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.movie " +
            "JOIN FETCH s.screen sc " +
            "JOIN FETCH sc.cinema " +
            "WHERE s.id = :id")
    Optional<Schedule> findByIdWithDetails(@Param("id") Long id);

}
