package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.domain.Movie;
import com.ceos23.spring_boot.dto.MovieRequest;
import com.ceos23.spring_boot.dto.MovieResponse;
import com.ceos23.spring_boot.global.response.SuccessResponse;
import com.ceos23.spring_boot.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    // 🎬 영화 생성
    @PostMapping
    public ResponseEntity<SuccessResponse<MovieResponse>> create(@RequestBody MovieRequest request) {
        Movie movie = movieService.create(request.title(), request.director());
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", new MovieResponse(movie)));
    }

    // 🎬 전체 조회
    @GetMapping
    public ResponseEntity<SuccessResponse<List<MovieResponse>>> getAll() {
        List<MovieResponse> movies = movieService.findAll()
                .stream()
                .map(MovieResponse::new)
                .toList();

        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", movies));
    }

    // 🎬 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<MovieResponse>> getOne(@PathVariable Long id) {
        Movie movie = movieService.findById(id);
        return ResponseEntity.ok(new SuccessResponse<>(200, "SUCCESS", new MovieResponse(movie)));
    }
}