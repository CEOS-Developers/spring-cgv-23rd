package com.ceos23.spring_boot.controller.movie.controller;

import com.ceos23.spring_boot.controller.movie.dto.MovieCreateRequest;
import com.ceos23.spring_boot.controller.movie.dto.MovieResponse;
import com.ceos23.spring_boot.controller.movie.dto.MovieSearchRequest;
import com.ceos23.spring_boot.controller.movie.dto.MovieUpdateRequest;
import com.ceos23.spring_boot.domain.movie.dto.MovieInfo;
import com.ceos23.spring_boot.domain.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "2. Movie (영화)", description = "영화 API")
@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "영화 목록 조회", description = "전체 영화 목록을 조회하거나 제목 등의 조건으로 필터링합니다.")
    @GetMapping("/api/movies")
    public ResponseEntity<List<MovieResponse>> findMovies(
            // 스웨거에서 쿼리 파라미터로 예쁘게 보여주기 위한 마법의 어노테이션!
            @ParameterObject
            @ModelAttribute MovieSearchRequest request) {

        List<MovieResponse> responses = movieService.findMovies(request.toCommand())
                .stream()
                .map(MovieResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "영화 조회", description = "아이디로 영화 단건을 상세 조회합니다.")
    @GetMapping("/api/movies/{movieId}")
    public ResponseEntity<MovieResponse> findMovie(
            @PathVariable Long movieId
    ) {
        MovieResponse response = MovieResponse.from(movieService.findMovie(movieId));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "영화 생성", description = "새로운 영화 정보를 등록합니다.")
    @PostMapping("/api/movies")
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieCreateRequest request) {
        MovieInfo info = movieService.createMovie(request.toCommand());
        MovieResponse response = MovieResponse.from(info);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "영화 수정", description = "영화의 상세 정보를 수정합니다.")
    @PatchMapping("/api/movies/{movieId}")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable Long movieId,
            @Valid @RequestBody MovieUpdateRequest request
    ) {
        MovieInfo info = movieService.updateMovie(movieId, request.toCommand());
        MovieResponse response = MovieResponse.from(info);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "영화 삭제", description = "영화 정보를 삭제합니다.")
    @DeleteMapping("/api/movies/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
