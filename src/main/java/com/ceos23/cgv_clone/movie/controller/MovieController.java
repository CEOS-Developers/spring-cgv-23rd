package com.ceos23.cgv_clone.movie.controller;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.movie.dto.response.MovieResponse;
import com.ceos23.cgv_clone.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/{movieId}")
    public ApiResponse<MovieResponse> getMovie(
            @PathVariable Long movieId
    ) {
        return movieService.getMovie(movieId);
    }
}
