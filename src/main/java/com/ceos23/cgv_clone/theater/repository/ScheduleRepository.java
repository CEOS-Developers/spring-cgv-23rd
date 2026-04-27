package com.ceos23.cgv_clone.theater.repository;

import com.ceos23.cgv_clone.theater.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByMovie_IdAndScreen_Theater_Id(Long movieId, Long screenTheaterId);
}
