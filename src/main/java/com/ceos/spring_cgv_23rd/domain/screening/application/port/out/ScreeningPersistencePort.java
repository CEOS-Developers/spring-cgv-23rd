package com.ceos.spring_cgv_23rd.domain.screening.application.port.out;

import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.HallInfoResult;
import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.MovieInfoResult;
import com.ceos.spring_cgv_23rd.domain.screening.domain.Screening;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ScreeningPersistencePort {

    List<Screening> findScreeningByMovieAndTheaterAndDate(Long movieId, Long theaterId, LocalDate date);

    List<Screening> findScreeningByTheaterAndDate(Long theaterId, LocalDate date);

    Map<Long, HallInfoResult> findHallInfoByIds(List<Long> hallIds);

    Map<Long, MovieInfoResult> findMovieInfoByIds(List<Long> movieIds);

    boolean existsMovieById(Long movieId);

    boolean existsTheaterById(Long theaterId);
}
