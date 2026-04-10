package com.ceos23.cgv.domain.movie.controller.user;

import com.ceos23.cgv.domain.movie.dto.MovieResponse;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.service.MovieService;
import com.ceos23.cgv.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Tag(name = "Movie API", description = "일반 고객용 영화 조회 API")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @Operation(summary = "전체 영화 조회")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(ApiResponse.success(movies, MovieResponse::from));
    }

    @GetMapping("/{movieId}")
    @Operation(summary = "특정 영화 단건 조회")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable Long movieId) {
        Movie movie = movieService.getMovieDetails(movieId);
        return ResponseEntity.ok(ApiResponse.success(MovieResponse.from(movie)));
    }
}