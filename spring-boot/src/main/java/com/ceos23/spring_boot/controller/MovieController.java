package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.domain.Movie;
import com.ceos23.spring_boot.dto.MovieRequest;
import com.ceos23.spring_boot.dto.MovieResponse;
import com.ceos23.spring_boot.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    // 🎬 영화 생성
    @PostMapping
    public MovieResponse create(@RequestBody MovieRequest request) {
        Movie movie = movieService.create(request.title(), request.director());
        return new MovieResponse(movie);
    }

    // 🎬 전체 조회
    @GetMapping
    public List<MovieResponse> getAll() {
        return movieService.findAll()
                .stream()
                .map(MovieResponse::new)
                .toList();
    }

    // 🎬 단건 조회
    @GetMapping("/{id}")
    public MovieResponse getOne(@PathVariable Long id) {
        return new MovieResponse(movieService.findById(id));
    }
}