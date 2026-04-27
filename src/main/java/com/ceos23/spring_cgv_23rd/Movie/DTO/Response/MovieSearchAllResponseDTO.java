package com.ceos23.spring_cgv_23rd.Movie.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record MovieSearchAllResponseDTO(
        List<MovieWrapperDTO> searchedMovies
) {
    public static List<MovieWrapperDTO> from(List<Movie> movie){
        return MovieWrapperDTO.create(movie);
    }
}
