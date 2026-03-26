package com.ceos23.spring_boot.domain.reservation.repository;

import com.ceos23.spring_boot.domain.reservation.entity.Schedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @EntityGraph(attributePaths = {"screen", "screen.screenType"})
    Optional<Schedule> findById(Long scheduleId);
}
