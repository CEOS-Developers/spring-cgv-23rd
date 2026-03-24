package com.ceos23.cgv_clone.movie.repository;

import com.ceos23.cgv_clone.movie.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
