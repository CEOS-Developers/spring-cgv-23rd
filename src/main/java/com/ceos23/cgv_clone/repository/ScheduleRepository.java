package com.ceos23.cgv_clone.repository;

import com.ceos23.cgv_clone.domain.movie.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
