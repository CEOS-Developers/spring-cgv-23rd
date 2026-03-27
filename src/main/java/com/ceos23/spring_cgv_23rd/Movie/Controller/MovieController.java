package com.ceos23.spring_cgv_23rd.Movie.Controller;

import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchAllResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.Service.MovieService;
import com.ceos23.spring_cgv_23rd.Screen.DTO.Response.ScreeningSearchResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movie")
public class MovieController {
    MovieService movieService;

    public MovieController(MovieService movieService){
        this.movieService = movieService;
    }

    @GetMapping("/{searchQuery}")
    public ResponseEntity<MovieSearchResponseDTO> searchWithName(
            @PathVariable String searchQuery
    ) {
        return movieService.theaterSearchService(searchQuery);
    }

    @GetMapping
    public ResponseEntity<MovieSearchAllResponseDTO> searchAll() {
        return movieService.theaterSearchService();
    }
}