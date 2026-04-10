package com.ceos23.spring_boot.dto;

import com.ceos23.spring_boot.domain.Movie;
import lombok.Getter;

@Getter
public class MovieResponse {

    private final String title;
    private final String director;

    public MovieResponse(Movie movie) {
        this.title = movie.getTitle();
        this.director = movie.getDirector();
    }
}