package com.ceos23.cgv.domain.movie.controller.admin;

import com.ceos23.cgv.domain.movie.dto.MovieCreateRequest;
import com.ceos23.cgv.domain.movie.dto.MovieResponse;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.service.MovieService;
import com.ceos23.cgv.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/v1/movies")
@RequiredArgsConstructor
@Tag(name = "Admin Movie API", description = "관리자 전용 영화 등록/삭제 API")
public class AdminMovieController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "새로운 영화 등록")
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@RequestBody MovieCreateRequest request) {
        Movie createdMovie = movieService.createMovie(
                request.title(), request.runningTime(), request.releaseDate(),
                request.movieRating(), request.genre(), request.prologue()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(MovieResponse.from(createdMovie)));
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "특정 영화 삭제")
    public ResponseEntity<ApiResponse<String>> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok(ApiResponse.success("영화가 성공적으로 삭제되었습니다."));
    }
}