package com.ceos.spring_boot.domain.movie.service;

import com.ceos.spring_boot.domain.cinema.dto.CinemaCreateRequest;
import com.ceos.spring_boot.domain.cinema.dto.CinemaResponse;
import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.movie.dto.MovieCreateRequest;
import com.ceos.spring_boot.domain.movie.dto.MovieListResponse;
import com.ceos.spring_boot.domain.movie.dto.MovieResponse;
import com.ceos.spring_boot.domain.movie.entity.Movie;
import com.ceos.spring_boot.domain.movie.repository.MovieRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
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
        Movie movie = Movie.builder()
                .title(request.title())
                .runningTime(request.runningTime())
                .genre(request.genre())
                .ageRating(request.ageRating())
                .releaseDate(request.releaseDate())
                .build();
        Movie savedMovie = movieRepository.save(movie);
        return MovieResponse.from(savedMovie);
    }

    // 모든 영화 조회
    public MovieListResponse findAllMovies() {
        List<MovieResponse> movieResponses = movieRepository.findAll().stream()
                .map(MovieResponse::from)
                .toList();
        return MovieListResponse.from(movieResponses);
    }

    // 영화 id로 영화 조회
    public MovieResponse findMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.MOVIE_NOT_FOUND_ERROR.getMessage()));
        return MovieResponse.from(movie);
    }

    // 영화 삭제
    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.MOVIE_NOT_FOUND_ERROR.getMessage()));
        movieRepository.delete(movie);
    }
}
