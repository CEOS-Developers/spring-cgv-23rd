package com.ceos23.spring_cgv_23rd.Movie.Service;

import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchAllResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieWrapperDTO;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {
    MovieRepository movieRepository;

    MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    @Transactional
    public ResponseEntity<MovieSearchResponseDTO> theaterSearchService(String query){
        List<Movie> searchedMovie = movieRepository.findByMovieNameContaining(query);

        MovieSearchResponseDTO responseDTO = MovieSearchResponseDTO.builder()
                .movie(MovieWrapperDTO.from(searchedMovie))
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    @Transactional
    public ResponseEntity<MovieSearchAllResponseDTO> theaterSearchService(){
        List<Movie> searchedMovies = movieRepository.findAll();

        MovieSearchAllResponseDTO responseDTO = MovieSearchAllResponseDTO.builder()
                .searchedMovies(MovieWrapperDTO.from(searchedMovies))
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
