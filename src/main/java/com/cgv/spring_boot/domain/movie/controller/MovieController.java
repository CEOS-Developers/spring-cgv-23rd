package com.cgv.spring_boot.domain.movie.controller;

import com.cgv.spring_boot.domain.movie.dto.request.MovieCreateRequest;
import com.cgv.spring_boot.domain.movie.dto.response.MovieResponse;
import com.cgv.spring_boot.domain.movie.service.MovieService;
import com.cgv.spring_boot.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Movie", description = "영화 관리 및 조회 관련 API")
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "영화 등록", description = "새로운 영화 정보를 시스템에 등록합니다.")
    @PostMapping("/")
    public ResponseEntity<Long> createMovie(@RequestBody MovieCreateRequest request) {
        return ResponseEntity.ok(movieService.saveMovie(request));
    }

    @Operation(summary = "전체 영화 목록 조회", description = "현재 등록된 모든 영화의 리스트를 반환합니다.")
    @GetMapping("/")
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        return ResponseEntity.ok(movieService.findAllMovies());
    }

    @Operation(summary = "영화 상세 조회", description = "영화 ID(PK)를 이용하여 특정 영화의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovie(@PathVariable("id") Long id) {
        MovieResponse response = movieService.findMovieById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "영화 삭제", description = "영화 ID를 이용하여 등록된 영화 정보를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteMovieById(id);
        return ResponseEntity.noContent().build();
    }
}
