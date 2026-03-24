package com.ceos.spring_cgv_23rd.domain.screening.service;

import com.ceos.spring_cgv_23rd.domain.movie.exception.MovieErrorCode;
import com.ceos.spring_cgv_23rd.domain.movie.repository.MovieRepository;
import com.ceos.spring_cgv_23rd.domain.screening.dto.ScreeningResponseDTO;
import com.ceos.spring_cgv_23rd.domain.screening.entity.Screening;
import com.ceos.spring_cgv_23rd.domain.screening.repository.ScreeningRepository;
import com.ceos.spring_cgv_23rd.domain.theater.exception.TheaterErrorCode;
import com.ceos.spring_cgv_23rd.domain.theater.repository.TheaterRepository;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;

    @Override
    public List<ScreeningResponseDTO.ScreeningByMovieResponseDTO> getScreeningByMovie(Long movieId, Long theaterId, LocalDate date) {

        // 영화 존재 여부 확인
        if (!movieRepository.existsById(movieId)) {
            throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
        }

        // 영화관 존재 여부 확인
        if (!theaterRepository.existsById(theaterId)) {
            throw new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND);
        }

        // 상영 일정 조회
        List<Screening> screenings = screeningRepository.findByMovieAndTheaterAndDate(movieId, theaterId, date);

        return ScreeningResponseDTO.ScreeningByMovieResponseDTO.from(screenings);
    }

    @Override
    public List<ScreeningResponseDTO.ScreeningByTheaterResponseDTO> getScreeningByTheater(Long theaterId, LocalDate date) {

        // 영화관 존재 여부 확인
        if (!theaterRepository.existsById(theaterId)) {
            throw new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND);
        }

        // 상영 일정 조회
        List<Screening> screenings = screeningRepository.findByTheaterAndDate(theaterId, date);

        return ScreeningResponseDTO.ScreeningByTheaterResponseDTO.from(screenings);
    }
}
