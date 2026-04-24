package com.ceos23.cgv.domain.movie.service;

import com.ceos23.cgv.domain.cinema.entity.Theater;
import com.ceos23.cgv.domain.cinema.repository.TheaterRepository;
import com.ceos23.cgv.domain.movie.dto.ScreeningCreateRequest;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.entity.Screening;
import com.ceos23.cgv.domain.movie.repository.MovieRepository;
import com.ceos23.cgv.domain.movie.repository.ScreeningRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;

    /**
     * [POST] 새로운 상영 일정 등록
     */
    @Transactional
    public Screening createScreening(ScreeningCreateRequest request) {
        Movie movie = findMovie(request.movieId());
        Theater theater = findTheater(request.theaterId());
        Screening screening = Screening.create(
                movie,
                theater,
                request.startTime(),
                request.endTime(),
                request.isMorning()
        );

        return screeningRepository.save(screening);
    }

    private Movie findMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
    }

    private Theater findTheater(Long theaterId) {
        return theaterRepository.findById(theaterId)
                .orElseThrow(() -> new CustomException(ErrorCode.THEATER_NOT_FOUND));
    }

    /**
     * [GET] 특정 영화의 상영 일정(시간표) 조회
     */
    public List<Screening> getScreeningsByMovieId(Long movieId) {
        return screeningRepository.findByMovieId(movieId);
    }
}
