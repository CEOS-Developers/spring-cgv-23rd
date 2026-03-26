package com.ceos23.cgv.domain.movie.controller;

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

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movie API", description = "영화 관련 생성, 조회, 삭제 API")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "새로운 영화 등록", description = "영화 정보를 입력받아 DB에 생성합니다.")
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@RequestBody MovieCreateRequest request) {
        Movie createdMovie = movieService.createMovie(
                request.title(),
                request.runningTime(),
                request.releaseDate(),
                request.movieRating(),
                request.genre(),
                request.prologue()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(MovieResponse.from(createdMovie)));
    }

    @GetMapping
    @Operation(summary = "전체 영화 조회", description = "DB에 저장된 모든 영화 목록을 가져옵니다.")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(ApiResponse.success(movies, MovieResponse::from));
    }

    @GetMapping("/{movieId}")
    @Operation(summary = "특정 영화 단건 조회", description = "영화 ID(PK)를 통해 영화 하나의 상세 정보를 가져옵니다.")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable Long movieId) {
        Movie movie = movieService.getMovieDetails(movieId);
        return ResponseEntity.ok(ApiResponse.success(MovieResponse.from(movie)));
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "특정 영화 삭제", description = "영화 ID를 통해 특정 영화를 DB에서 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok(ApiResponse.success("영화가 성공적으로 삭제되었습니다."));
    }
}