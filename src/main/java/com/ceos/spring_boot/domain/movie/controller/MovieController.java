package com.ceos.spring_boot.domain.movie.controller;

import com.ceos.spring_boot.domain.movie.dto.MovieListResponse;
import com.ceos.spring_boot.domain.movie.dto.MovieResponse;
import com.ceos.spring_boot.domain.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Movie 관련 API", description = "영화 조회 및 관리를 위한 API입니다.")
@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // 모든 영화 조회
    @Operation(summary = "모든 영화 조회", description = "모든 영화를 조회합니다. ")
    @GetMapping("/movies")
    public ResponseEntity<MovieListResponse> getAllMovies() {
        return ResponseEntity.ok(movieService.findAllMovies());
    }

    // 영화 id로 특정 영화 조회
    @Operation(summary = "특정 영화 상세 조회", description = "영화 ID를 이용해 개별 영화 정보를 조회합니다.")
    @GetMapping("/movies/{id}")
    public ResponseEntity<MovieResponse> getMovieById(
            @Parameter(description = "조회할 영화의 ID")
            @PathVariable Long id) {
        return ResponseEntity.ok(movieService.findMovieById(id));
    }
}
