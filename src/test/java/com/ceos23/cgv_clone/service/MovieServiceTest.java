package com.ceos23.cgv_clone.service;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.common.codes.ErrorCode;
import com.ceos23.cgv_clone.config.exception.CustomException;
import com.ceos23.cgv_clone.movie.domain.Movie;
import com.ceos23.cgv_clone.movie.dto.response.MovieResponse;
import com.ceos23.cgv_clone.movie.repository.MovieRepository;
import com.ceos23.cgv_clone.movie.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    @Test
    @DisplayName("존재하는 영화 조회 시 영화 정보 반환")
    void 영화조회_성공() {
        // given
        Long movieId = 1L;
        Movie movie = Movie.builder()
                .name("프로젝트 헤일메리")
                .runningTime(156)
                .ageRestriction(12)
                .build();
        ReflectionTestUtils.setField(movie, "id", movieId);

        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));

        // when
        ApiResponse<MovieResponse> response = movieService.getMovie(movieId);

        // then
        // 코드 및 반환 값 확인
        assertTrue(response.isSuccess());
        assertEquals(200, response.getResultCode());
        assertEquals("SELECT SUCCESS", response.getResultMsg());
        assertNotNull(response.getResult());

        // 값 확인
        MovieResponse result = response.getResult();
        assertEquals(movieId, result.getId());
        assertEquals("프로젝트 헤일메리", result.getName());
        assertEquals(156, result.getRunningTime());
        assertEquals(12, result.getAgeRestriction());

        // movieStatistic이 없으면 0으로 매핑되는지 확인
        assertEquals(0, result.getReservationRate());
        assertEquals(0, result.getTotalViewers());
        assertEquals(0, result.getEggRate());
    }

    @Test
    @DisplayName("존재하지 않는 영화 조회 시 MOVIE_NOT_FOUND 예외 발생")
    void 영화조회_실패_영화없음() {
        // given
        Long movieId = 999L;
        given(movieRepository.findById(movieId)).willReturn(Optional.empty());

        // when
        CustomException ex = assertThrows(CustomException.class, () -> movieService.getMovie(movieId));

        // then
        assertEquals(ErrorCode.MOVIE_NOT_FOUND, ex.getErrorCode());
    }
}
