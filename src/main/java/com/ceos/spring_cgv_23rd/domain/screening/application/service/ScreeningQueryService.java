package com.ceos.spring_cgv_23rd.domain.screening.application.service;

import com.ceos.spring_cgv_23rd.domain.movie.exception.MovieErrorCode;
import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.HallInfoResult;
import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.MovieInfoResult;
import com.ceos.spring_cgv_23rd.domain.screening.application.dto.result.ScreeningDetailResult;
import com.ceos.spring_cgv_23rd.domain.screening.application.port.in.GetScreeningScheduleUseCase;
import com.ceos.spring_cgv_23rd.domain.screening.application.port.out.ScreeningPersistencePort;
import com.ceos.spring_cgv_23rd.domain.screening.domain.Screening;
import com.ceos.spring_cgv_23rd.domain.theater.exception.TheaterErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreeningQueryService implements GetScreeningScheduleUseCase {

    private final ScreeningPersistencePort screeningPersistencePort;


    @Override
    public List<ScreeningDetailResult> getScreeningByMovie(Long movieId, Long theaterId, LocalDate date) {

        // 영화 존재 여부 확인
        if (!screeningPersistencePort.existsMovieById(movieId)) {
            throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
        }

        // 영화관 존재 여부 확인
        if (!screeningPersistencePort.existsTheaterById(theaterId)) {
            throw new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND);
        }

        // 상영 일정 조회
        List<Screening> screenings = screeningPersistencePort.findScreeningByMovieAndTheaterAndDate(movieId, theaterId, date);

        // 상영관 정보 조회
        List<Long> hallIds = screenings.stream()
                .map(Screening::getHallId)
                .distinct()
                .toList();

        Map<Long, HallInfoResult> hallInfoMap = screeningPersistencePort.findHallInfoByIds(hallIds);

        return buildDetailResults(screenings, Map.of(), hallInfoMap);
    }

    @Override
    public List<ScreeningDetailResult> getScreeningByTheater(Long theaterId, LocalDate date) {

        // 영화관 존재 여부 확인
        if (!screeningPersistencePort.existsTheaterById(theaterId)) {
            throw new GeneralException(TheaterErrorCode.THEATER_NOT_FOUND);
        }

        // 상영 일정 조회
        List<Screening> screenings = screeningPersistencePort.findScreeningByTheaterAndDate(theaterId, date);

        // 영화 + 상영관 조회
        List<Long> movieIds = screenings.stream()
                .map(Screening::getMovieId)
                .distinct()
                .toList();

        List<Long> hallIds = screenings.stream()
                .map(Screening::getHallId)
                .distinct()
                .toList();

        Map<Long, MovieInfoResult> movieInfoMap = screeningPersistencePort.findMovieInfoByIds(movieIds);
        Map<Long, HallInfoResult> hallInfoMap = screeningPersistencePort.findHallInfoByIds(hallIds);

        return buildDetailResults(screenings, movieInfoMap, hallInfoMap);
    }

    private List<ScreeningDetailResult> buildDetailResults(
            List<Screening> screenings,
            Map<Long, MovieInfoResult> movieInfoMap,
            Map<Long, HallInfoResult> hallInfoMap) {

        return screenings.stream()
                .map(s -> {
                    HallInfoResult hallInfo = hallInfoMap.get(s.getHallId());
                    MovieInfoResult movieInfo = movieInfoMap.get(s.getMovieId());

                    return new ScreeningDetailResult(
                            s.getId(),
                            s.getMovieId(),
                            movieInfo != null ? movieInfo.movieTitle() : null,
                            movieInfo != null ? movieInfo.posterUrl() : null,
                            movieInfo != null ? movieInfo.ageRating() : null,
                            s.getHallId(),
                            hallInfo.hallName(),
                            hallInfo.hallTypeName(),
                            s.getStartAt(), s.getEndAt(),
                            s.getTotalSeats(), s.getRemainingSeats());
                })
                .toList();
    }
}
