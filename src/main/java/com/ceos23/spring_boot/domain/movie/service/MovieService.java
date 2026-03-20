package com.ceos23.spring_boot.domain.movie.service;

import com.ceos23.spring_boot.domain.movie.dto.MovieCreateCommand;
import com.ceos23.spring_boot.domain.movie.dto.MovieInfo;
import com.ceos23.spring_boot.domain.movie.dto.MovieSearchCommand;
import com.ceos23.spring_boot.domain.movie.dto.MovieUpdateCommand;
import com.ceos23.spring_boot.domain.movie.entity.Movie;
import com.ceos23.spring_boot.domain.movie.repository.MovieRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    public List<MovieInfo> findMovies(MovieSearchCommand command) {
        List<Movie> movies;

        if (StringUtils.hasText(command.title())) {
            movies = movieRepository.findByTitleContaining(command.title());
        } else {
            movies = movieRepository.findAll();
        }

        return movies.stream()
                .map(MovieInfo::from)
                .toList();
    }

    public MovieInfo findMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        return MovieInfo.from(movie);
    }

    @Transactional
    public MovieInfo createMovie(MovieCreateCommand command) {
        Movie movie = Movie.builder()
                .title(command.title())
                .runtime(command.runtime())
                .releaseDate(command.releaseDate())
                .ageRating(command.ageRating())
                .posterUrl(command.posterUrl())
                .description(command.description())
                .build();

        movieRepository.save(movie);

        return MovieInfo.from(movie);
    }

    @Transactional
    public MovieInfo updateMovie(Long id, MovieUpdateCommand command) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        movie.update(
                command.title(),
                command.runtime(),
                command.releaseDate(),
                command.ageRating(),
                command.posterUrl(),
                command.description()
        );

        return MovieInfo.from(movie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        movieRepository.delete(movie);
    }


}
