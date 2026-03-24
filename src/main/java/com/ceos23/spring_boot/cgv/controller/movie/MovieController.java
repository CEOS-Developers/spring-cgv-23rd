package com.ceos23.spring_boot.cgv.controller.movie;

import com.ceos23.spring_boot.cgv.dto.movie.MovieResponse;
import com.ceos23.spring_boot.cgv.service.movie.MovieService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public List<MovieResponse> getMovies() {
        return movieService.getMovies().stream()
                .map(MovieResponse::from)
                .toList();
    }

    @GetMapping("/{movieId}")
    public MovieResponse getMovie(@PathVariable Long movieId) {
        return MovieResponse.from(movieService.getMovie(movieId));
    }
}