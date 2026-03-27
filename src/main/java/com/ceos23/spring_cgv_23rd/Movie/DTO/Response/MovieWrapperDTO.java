package com.ceos23.spring_cgv_23rd.Movie.DTO.Response;

import com.ceos23.spring_cgv_23rd.Movie.Domain.*;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

public record MovieWrapperDTO(
    long id, List<CommentWrapperDTO> comments, String movieName, String prolog
) {
    public static MovieWrapperDTO create(Movie movie){
        return new MovieWrapperDTO(
                movie.getId(), CommentWrapperDTO.create(movie.getComments()), movie.getMovieName(), movie.getProlog()
        );
    }

    public static List<MovieWrapperDTO> create(List<Movie> movie){
        List<MovieWrapperDTO> res = new ArrayList<>();

        for (Movie m : movie){
            res.add(MovieWrapperDTO.create(m));
        }

        return res;
    }
}
