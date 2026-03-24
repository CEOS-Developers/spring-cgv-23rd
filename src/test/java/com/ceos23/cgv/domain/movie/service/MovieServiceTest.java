package com.ceos23.cgv.domain.movie.service;

import com.ceos23.cgv.domain.movie.dto.MovieCreateRequest;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.enums.Genre;
import com.ceos23.cgv.domain.movie.enums.MovieRating;
import com.ceos23.cgv.domain.movie.repository.MovieRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    @DisplayName("영화를 정상적으로 등록한다")
    void createMovie_Success() {
        // Given
        MovieCreateRequest request = new MovieCreateRequest(
                "아바타: 물의 길",
                192,
                LocalDate.of(2022, 12, 14),
                MovieRating.ALL,
                Genre.SF,
                "판도라 행성에서 벌어지는 새로운 이야기..."
        );

        given(movieRepository.save(any(Movie.class))).willAnswer(i -> i.getArgument(0));

        // When
        Movie savedMovie = movieService.createMovie(
                request.title(),
                request.runningTime(),
                request.releaseDate(),
                request.movieRating(),
                request.genre(),
                request.prologue()
        );

        // Then
        assertThat(savedMovie.getTitle()).isEqualTo("아바타: 물의 길");
        assertThat(savedMovie.getRunningTime()).isEqualTo(192);
        assertThat(savedMovie.getGenre()).isEqualTo(Genre.SF);

        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    @DisplayName("존재하지 않는 영화 조회 시 MOVIE_NOT_FOUND 예외가 발생한다")
    void getMovie_Fail_NotFound() {
        // Given
        Long invalidMovieId = 999L;
        given(movieRepository.findById(invalidMovieId)).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            movieService.getMovieDetails(invalidMovieId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MOVIE_NOT_FOUND);
    }
}