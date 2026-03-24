package com.ceos23.spring_cgv_23rd.Movie.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.*;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record MovieWrapperDTO(
    long id, List<CommentWrapperDTO> comments, String movieName, String prolog
) {
    public static MovieWrapperDTO from(Movie movie){
        return MovieWrapperDTO.builder()
                .id(movie.getId())
                .comments(CommentWrapperDTO.from(movie.getComments()))
                .movieName(movie.getMovieName())
                .prolog(movie.getProlog())
                .build();
    }

    public static List<MovieWrapperDTO> from(List<Movie> movie){
        List<MovieWrapperDTO> res = new ArrayList<>();

        for (Movie m : movie){
            res.add(MovieWrapperDTO.from(m));
        }

        return res;
    }
}
