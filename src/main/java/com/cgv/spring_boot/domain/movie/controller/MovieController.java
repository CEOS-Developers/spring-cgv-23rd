package com.cgv.spring_boot.domain.movie.controller;

import com.cgv.spring_boot.domain.movie.dto.request.MovieCreateRequest;
import com.cgv.spring_boot.domain.movie.dto.response.MovieResponse;
import com.cgv.spring_boot.domain.movie.service.MovieService;
import com.cgv.spring_boot.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/")
    public ResponseEntity<Long> createMovie(@RequestBody MovieCreateRequest request) {
        return ResponseEntity.ok(movieService.saveMovie(request));
    }

    @GetMapping("/")
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        return ResponseEntity.ok(movieService.findAllMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovie(@PathVariable("id") Long id) {
        MovieResponse response = movieService.findMovieById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteMovieById(id);
        return ResponseEntity.noContent().build();
    }
}
