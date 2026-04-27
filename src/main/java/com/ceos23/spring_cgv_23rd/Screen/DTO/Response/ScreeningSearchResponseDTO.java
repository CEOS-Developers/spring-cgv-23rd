package com.ceos23.spring_cgv_23rd.Screen.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.AccessibleAge;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.util.List;

public record ScreeningSearchResponseDTO(
        long movieId, String movieName, AccessibleAge age,
        List<ScreenWrapperDTO> screen
) {
    public static ScreeningSearchResponseDTO create(Movie movie,
                                                    List<ScreenWrapperDTO> screens){
        return new ScreeningSearchResponseDTO(
                movie.getId(), movie.getMovieName(), movie.getAccessibleAge(), screens
        );
    }
}