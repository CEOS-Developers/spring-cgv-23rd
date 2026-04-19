package com.ceos23.cgv_clone.theater.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.movie.repository.MovieRepository;
import com.ceos23.cgv_clone.theater.entity.Schedule;
import com.ceos23.cgv_clone.theater.dto.response.ScheduleResponse;
import com.ceos23.cgv_clone.theater.repository.ScheduleRepository;
import com.ceos23.cgv_clone.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedule(Long movieId, Long theaterId) {
        theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));

        movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        List<Schedule> response = scheduleRepository.findAllByMovie_IdAndScreen_Theater_Id(movieId, theaterId);

        return response.stream()
                .map(ScheduleResponse::from)
                .toList();
    }
}
