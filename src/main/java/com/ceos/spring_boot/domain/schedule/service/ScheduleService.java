package com.ceos.spring_boot.domain.schedule.service;


import com.ceos.spring_boot.domain.cinema.entity.Screen;
import com.ceos.spring_boot.domain.cinema.repository.ScreenRepository;
import com.ceos.spring_boot.domain.movie.entity.Movie;
import com.ceos.spring_boot.domain.movie.repository.MovieRepository;
import com.ceos.spring_boot.domain.schedule.dto.ScheduleRequest;
import com.ceos.spring_boot.domain.schedule.dto.ScheduleResponse;
import com.ceos.spring_boot.domain.schedule.entity.Schedule;
import com.ceos.spring_boot.domain.schedule.repository.ScheduleRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;

    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.MOVIE_NOT_FOUND_ERROR.getMessage()));
        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.SCREEN_NOT_FOUND_ERROR.getMessage()));

        // 종료 시간 계산 (시작 시간 + 러닝타임)
        LocalDateTime endAt = request.startAt().plusMinutes(movie.getRunningTime());

        // 시간 중복 검증
        if (!scheduleRepository.findOverlappingSchedules(screen.getId(), request.startAt(), endAt).isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.ALREADY_SCREEN_SCHEDULE_ERROR.getMessage());
        }

        Schedule schedule = Schedule.builder()
                .movie(movie)
                .screen(screen)
                .startAt(request.startAt())
                .endAt(endAt)
                .build();

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    // 상영 일정 삭제
    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
