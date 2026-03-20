package com.ceos23.spring_boot.domain.reservation.repository;

import com.ceos23.spring_boot.domain.reservation.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
