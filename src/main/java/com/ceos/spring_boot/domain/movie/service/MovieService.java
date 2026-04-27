package com.ceos.spring_boot.domain.movie.service;

import com.ceos.spring_boot.domain.cinema.dto.CinemaCreateRequest;
import com.ceos.spring_boot.domain.cinema.dto.CinemaListResponse;
import com.ceos.spring_boot.domain.cinema.dto.CinemaResponse;
import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.movie.dto.MovieCreateRequest;
import com.ceos.spring_boot.domain.movie.dto.MovieListResponse;
import com.ceos.spring_boot.domain.movie.dto.MovieResponse;
import com.ceos.spring_boot.domain.movie.entity.Movie;
import com.ceos.spring_boot.domain.movie.repository.MovieRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;

    // 영화 생성
    @Transactional
    public MovieResponse createMovie(MovieCreateRequest request) {
        Movie movie = Movie.create(
                request.title(),
                request.runningTime(),
                request.genre(),
                request.releaseDate(),
                request.ageRating()
        );

        return MovieResponse.from(movieRepository.save(movie));
    }

    // 모든 영화 조회
    public MovieListResponse findAllMovies() {
        return MovieListResponse.from(
                movieRepository.findAll().stream().map(MovieResponse::from).toList()
        );
    }

    // 영화 id로 영화 조회
    public MovieResponse findMovieById(Long id) {
        return MovieResponse.from(findEntityById(id));
    }

    // 영화 삭제
    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.delete(findEntityById(id));
    }

    // 내부에서 공통으로 사용하는 엔티티 조회 로직 분리 (-> 중복 제거)
    private Movie findEntityById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND_ERROR));
    }
}
