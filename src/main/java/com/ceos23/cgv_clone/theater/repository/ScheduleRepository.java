package com.ceos23.cgv_clone.theater.repository;

import com.ceos23.cgv_clone.theater.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByMovie_IdAndScreen_Theater_Id(Long movieId, Long screenTheaterId);

    @Query("""
            SELECT s
            FROM Schedule s
            JOIN FETCH s.movie
            JOIN FETCH s.screen sc
            JOIN FETCH sc.screenType
            where s.id = :scheduleId
    """)
    Optional<Schedule> findByIdWithMovieAndScreenType(Long scheduleId);
}
