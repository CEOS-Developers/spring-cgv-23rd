package com.ceos23.spring_cgv_23rd.Movie.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MovieSearchResponseDTO {
    private List<MovieWrapperDTO> movie;

    public static MovieSearchResponseDTO from(MovieWrapperDTO movie){
        return MovieSearchResponseDTO.builder()
                .movie(List.of(movie))
                .build();
    }

    public static List<MovieWrapperDTO> from(List<Movie> movies){
        return MovieWrapperDTO.create(movies);
    }
}