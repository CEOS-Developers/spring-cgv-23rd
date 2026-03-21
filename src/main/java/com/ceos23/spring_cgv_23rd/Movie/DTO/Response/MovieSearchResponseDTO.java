package com.ceos23.spring_cgv_23rd.Movie.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
public record MovieSearchResponseDTO(
        List<MovieWrapperDTO> movie
) {
    public static List<MovieWrapperDTO> from(Movie movie){
        return Collections.singletonList(MovieWrapperDTO.from(movie));
    }

    public static List<MovieWrapperDTO> from(List<Movie> movies){
        List<MovieWrapperDTO> res = new ArrayList<>();
        return MovieWrapperDTO.from(movies);
    }
}
