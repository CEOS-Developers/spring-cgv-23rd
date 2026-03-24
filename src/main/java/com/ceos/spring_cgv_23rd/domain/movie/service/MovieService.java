package com.ceos.spring_cgv_23rd.domain.movie.service;

import com.ceos.spring_cgv_23rd.domain.movie.dto.MovieResponseDTO;

import java.util.List;

public interface MovieService {

    List<MovieResponseDTO.MovieListResponseDTO> getMovieChart();

    List<MovieResponseDTO.MovieListResponseDTO> getRunningMovie();

    List<MovieResponseDTO.MovieListResponseDTO> getUpcomingMovie();

    MovieResponseDTO.MovieDetailResponseDTO getMovieDetail(Long movieId);

    List<MovieResponseDTO.MovieCreditResponseDTO> getMovieCredits(Long movieId);

    List<MovieResponseDTO.MovieMediaResponseDTO> getMovieMedias(Long movieId);

    // 영화 찜
    MovieResponseDTO.MovieLikeResponseDTO toggleMovieLike(Long userId, Long movieId);
}
