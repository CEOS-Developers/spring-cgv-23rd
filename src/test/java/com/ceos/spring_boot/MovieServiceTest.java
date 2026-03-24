package com.ceos.spring_boot;

import com.ceos.spring_boot.domain.movie.dto.MovieListResponse;

import com.ceos.spring_boot.domain.movie.entity.Movie;
import com.ceos.spring_boot.domain.movie.repository.MovieRepository;
import com.ceos.spring_boot.domain.movie.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    @DisplayName("모든 영화 조회 성공 테스트")
    void findAllMovies_success() {
        // given
        Movie movie = Movie.builder().id(1L).title("왕과 사는 남자").build();
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        // when
        MovieListResponse result = movieService.findAllMovies();

        // then
        assertThat(result.movies()).hasSize(1);
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("존재하지 않는 영화 ID 조회 시 예외 발생")
    void findMovieById_fail() {
        // given
        Long invalidId = 99L;
        when(movieRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> movieService.findMovieById(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 ID의 영화를 찾을 수 없습니다");
    }
}