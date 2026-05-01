package com.ceos23.cgv_clone.theater.repository;

import com.ceos23.cgv_clone.theater.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("""
            SELECT s
            FROM Schedule s
            JOIN FETCH s.movie
            JOIN FETCH s.screen sc
            JOIN FETCH sc.screenType
            where s.id = :scheduleId
            """)
    Optional<Schedule> findByIdWithMovieAndScreenType(Long scheduleId);

    @Query("""
            SELECT s
            FROM Schedule s
            JOIN FETCH s.movie
            JOIN FETCH s.screen sc
            JOIN FETCH sc.theater
            WHERE s.movie.id = :movieId
                AND sc.theater.id = :theaterId
            """)
    List<Schedule> findAllByMovieIdAndScreenTheaterId(Long movieId, Long theaterId);
}
