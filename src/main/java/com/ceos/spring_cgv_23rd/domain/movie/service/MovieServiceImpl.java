package com.ceos.spring_cgv_23rd.domain.movie.service;

import com.ceos.spring_cgv_23rd.domain.movie.dto.MovieResponseDTO;
import com.ceos.spring_cgv_23rd.domain.movie.entity.Movie;
import com.ceos.spring_cgv_23rd.domain.movie.entity.MovieStatistic;
import com.ceos.spring_cgv_23rd.domain.movie.enums.MovieStatus;
import com.ceos.spring_cgv_23rd.domain.movie.exception.MovieErrorCode;
import com.ceos.spring_cgv_23rd.domain.movie.repository.MovieCreditRepository;
import com.ceos.spring_cgv_23rd.domain.movie.repository.MovieMediaRepository;
import com.ceos.spring_cgv_23rd.domain.movie.repository.MovieRepository;
import com.ceos.spring_cgv_23rd.domain.movie.repository.MovieStatisticRepository;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieStatisticRepository movieStatisticRepository;
    private final MovieCreditRepository movieCreditRepository;
    private final MovieMediaRepository movieMediaRepository;

    @Override
    public List<MovieResponseDTO.MovieListResponseDTO> getMovieChart() {

        // 상영중/상영예정 영화 조회
        List<Object[]> results = movieRepository.findWithStatisticByStatusIn(
                List.of(MovieStatus.RUNNING, MovieStatus.UPCOMING));

        return results.stream()
                .map(res -> MovieResponseDTO.MovieListResponseDTO.of((Movie) res[0], (MovieStatistic) res[1]))
                .toList();
    }

    @Override
    public List<MovieResponseDTO.MovieListResponseDTO> getRunningMovie() {

        // 상영 중인 영화 조회
        List<Object[]> results = movieRepository.findWithStatisticByStatus(MovieStatus.RUNNING);

        return results.stream()
                .map(res -> MovieResponseDTO.MovieListResponseDTO.of((Movie) res[0], (MovieStatistic) res[1]))
                .toList();
    }

    @Override
    public List<MovieResponseDTO.MovieListResponseDTO> getUpcomingMovie() {

        // 상영 예정 영화 조회
        List<Object[]> results = movieRepository.findWithStatisticByStatus(MovieStatus.UPCOMING);

        return results.stream()
                .map(res -> MovieResponseDTO.MovieListResponseDTO.of((Movie) res[0], (MovieStatistic) res[1]))
                .toList();
    }

    @Override
    public MovieResponseDTO.MovieDetailResponseDTO getMovieDetail(Long movieId) {

        // 영화 조회
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND));

        // 영화 통계 조회
        MovieStatistic statistic = movieStatisticRepository.findByMovieId(movieId)
                .orElseThrow(() -> new GeneralException(MovieErrorCode.MOVIE_STAT_NOT_FOUND));


        return MovieResponseDTO.MovieDetailResponseDTO.of(movie, statistic);
    }

    @Override
    public List<MovieResponseDTO.MovieCreditResponseDTO> getMovieCredits(Long movieId) {

        if (!movieRepository.existsById(movieId)) {
            throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
        }

        return movieCreditRepository.findByMovieIdWithContributor(movieId).stream()
                .map(MovieResponseDTO.MovieCreditResponseDTO::from)
                .toList();
    }

    @Override
    public List<MovieResponseDTO.MovieMediaResponseDTO> getMovieMedias(Long movieId) {

        if (!movieRepository.existsById(movieId)) {
            throw new GeneralException(MovieErrorCode.MOVIE_NOT_FOUND);
        }

        return movieMediaRepository.findByMovieId(movieId).stream()
                .map(MovieResponseDTO.MovieMediaResponseDTO::from)
                .toList();
    }
}
