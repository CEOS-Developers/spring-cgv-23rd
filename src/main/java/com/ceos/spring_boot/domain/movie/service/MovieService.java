package com.ceos.spring_boot.domain.movie.service;

import com.ceos.spring_boot.domain.movie.dto.MovieListResponse;
import com.ceos.spring_boot.domain.movie.dto.MovieResponse;
import com.ceos.spring_boot.domain.movie.entity.Movie;
import com.ceos.spring_boot.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;

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
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 영화를 찾을 수 없습니다" ));
        return MovieResponse.from(movie);
    }
}
