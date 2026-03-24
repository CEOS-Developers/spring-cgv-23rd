package com.ceos23.spring_boot.controller;

import com.ceos23.spring_boot.domain.Movie;
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
    public Movie create(@RequestBody MovieRequest request) {
        return movieService.create(request.title());
    }

    // 🎬 전체 조회
    @GetMapping
    public List<Movie> getAll() {
        return movieService.findAll();
    }

    // 🎬 단건 조회
    @GetMapping("/{id}")
    public Movie getOne(@PathVariable Long id) {
        return movieService.findById(id);
    }
}