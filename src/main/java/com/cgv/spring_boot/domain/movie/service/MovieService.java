package com.cgv.spring_boot.domain.movie.service;

import com.cgv.spring_boot.domain.movie.dto.request.MovieCreateRequest;
import com.cgv.spring_boot.domain.movie.dto.response.MovieResponse;
import com.cgv.spring_boot.domain.movie.entity.Movie;
import com.cgv.spring_boot.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    
    @Transactional
    public Long saveMovie(MovieCreateRequest request) {
        Movie movie = request.toEntity();
        return movieRepository.save(movie).getId();
    }

    public List<MovieResponse> findAllMovies() {
        return movieRepository.findAll().stream()
                .map(MovieResponse::from)
                .toList();
    }

    public MovieResponse findMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 영화가 존재하지 않습니다. ID: " + id));
        return MovieResponse.from(movie);
    }

    @Transactional
    public void deleteMovieById(Long id) {
        movieRepository.deleteById(id);
    }
}
