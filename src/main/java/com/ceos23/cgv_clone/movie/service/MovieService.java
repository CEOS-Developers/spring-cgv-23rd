package com.ceos23.cgv_clone.movie.service;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.common.codes.ErrorCode;
import com.ceos23.cgv_clone.common.codes.SuccessCode;
import com.ceos23.cgv_clone.config.exception.CustomException;
import com.ceos23.cgv_clone.movie.domain.Movie;
import com.ceos23.cgv_clone.movie.dto.response.MovieResponse;
import com.ceos23.cgv_clone.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    // 영화 상세 조회
    @Transactional(readOnly = true)
    public ApiResponse<MovieResponse> getMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        return ApiResponse.ok(SuccessCode.SELECT_SUCCESS, MovieResponse.from(movie));
    }
}
