package com.ceos23.cgv_clone.movie.service;

import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.movie.entity.Movie;
import com.ceos23.cgv_clone.movie.dto.response.MovieResponse;
import com.ceos23.cgv_clone.movie.repository.MovieRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    // 영화 상세 조회
	@Cacheable(value = "movieDetail", key = "#movieId")
    @Transactional(readOnly = true)
    public MovieResponse getMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        return MovieResponse.from(movie);
    }
}
