package com.ceos.spring_boot.domain.movie.controller;

import com.ceos.spring_boot.domain.cinema.dto.CinemaCreateRequest;
import com.ceos.spring_boot.domain.cinema.dto.CinemaResponse;
import com.ceos.spring_boot.domain.movie.dto.MovieCreateRequest;
import com.ceos.spring_boot.domain.movie.dto.MovieListResponse;
import com.ceos.spring_boot.domain.movie.dto.MovieResponse;
import com.ceos.spring_boot.domain.movie.service.MovieService;
import com.ceos.spring_boot.global.codes.SuccessCode;
import com.ceos.spring_boot.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Movie 관련 API", description = "영화 조회 및 관리를 위한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    // 영화 생성
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "영화 생성", description = "영화를 생성합니다.")
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@RequestBody @Valid MovieCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(movieService.createMovie(request), SuccessCode.INSERT_SUCCESS));
    }

    // 모든 영화 조회
    @GetMapping
    @Operation(summary = "모든 영화 조회", description = "모든 영화를 조회합니다. ")
    public ResponseEntity<ApiResponse<MovieListResponse>> getAllMovies() {
        MovieListResponse response = movieService.findAllMovies();
        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.GET_SUCCESS));
    }

    // 영화 id로 특정 영화 조회
    @GetMapping("/{movieId}")
    @Operation(summary = "특정 영화 상세 조회", description = "영화 ID를 이용해 개별 영화 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(
            @Parameter(description = "조회할 영화의 ID")
            @PathVariable Long movieId) {
        MovieResponse response = movieService.findMovieById(movieId);
        return ResponseEntity.ok(ApiResponse.of(response, SuccessCode.GET_SUCCESS));
    }

    @DeleteMapping("/{movieId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "영화 삭제", description = "영화 ID를 이용해 영화를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok(ApiResponse.of(null, SuccessCode.DELETE_SUCCESS));
    }
}
