package com.ceos.spring_boot.domain.schedule.repository;

import com.ceos.spring_boot.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 영화의 특정 날짜 상영 일정 조회
    List<Schedule> findByMovieIdAndStartDate(Long movieId, LocalDate startDate);

    // 특정 영화관의 특정 날짜 모든 일정 조회
    List<Schedule> findByScreen_Cinema_IdAndStartDate(Long cinemaId, LocalDate startDate);

    // 특정 영화, 특정 지점, 특정 날짜의 일정 조회
    List<Schedule> findByMovieIdAndScreen_Cinema_IdAndStartDateOrderByStartTimeAsc(
            Long movieId, Long cinemaId, LocalDate startDate
    );

}
