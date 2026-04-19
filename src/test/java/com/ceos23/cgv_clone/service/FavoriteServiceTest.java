package com.ceos23.cgv_clone.service;

import com.ceos23.cgv_clone.favorite.dto.response.FavoriteResponse;
import com.ceos23.cgv_clone.favorite.repository.MovieFavoriteRepository;
import com.ceos23.cgv_clone.favorite.repository.TheaterFavoriteRepository;
import com.ceos23.cgv_clone.favorite.service.FavoriteService;
import com.ceos23.cgv_clone.movie.entity.Movie;
import com.ceos23.cgv_clone.movie.repository.MovieRepository;
import com.ceos23.cgv_clone.theater.entity.Theater;
import com.ceos23.cgv_clone.theater.repository.TheaterRepository;
import com.ceos23.cgv_clone.user.entity.User;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @InjectMocks
    private FavoriteService favoriteService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TheaterRepository theaterRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private TheaterFavoriteRepository theaterFavoriteRepository;
    @Mock
    private MovieFavoriteRepository movieFavoriteRepository;

    @Test
    @DisplayName("영화관 찜이 이미 존재하면 찜 해제")
    void 영화관찜_해제_성공() {
        // given
        Long userId = 1L;
        Long theaterId = 10L;

        User user = User.builder()
                        .nickname("jong")
                        .email("jong@test.com")
                        .birthdate(LocalDate.of(2000, 1, 28))
                        .build();

        Theater theater = Theater.builder()
                        .name("CGV 강남")
                        .region("서울")
                        .address("서울 강남구")
                        .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(theaterRepository.findById(theaterId)).willReturn(Optional.of(theater));
        given(theaterFavoriteRepository.existsByUserAndTheater(user, theater)).willReturn(true);

        // when
        FavoriteResponse response = favoriteService.toggleFavoriteTheater(userId, theaterId);

        // then
        assertFalse(response.isFavorite());
    }

    @Test
    @DisplayName("영화관 찜이 없고 5개 미만이면 찜 추가")
    void 영화관찜_성공() {
        // given
        Long userId = 1L;
        Long theaterId = 10L;

        User user = User.builder()
                        .nickname("jong")
                        .email("jong@test.com")
                        .birthdate(LocalDate.of(2000, 1, 28))
                        .build();

        Theater theater = Theater.builder()
                        .name("CGV 강남")
                        .region("서울")
                        .address("서울 강남구")
                        .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(theaterRepository.findById(theaterId)).willReturn(Optional.of(theater));
        given(theaterFavoriteRepository.existsByUserAndTheater(user, theater)).willReturn(false);
        given(theaterFavoriteRepository.countByUser(user)).willReturn(3);

        // when
        FavoriteResponse response = favoriteService.toggleFavoriteTheater(userId, theaterId);

        // then
        assertTrue(response.isFavorite());
    }

    @Test
    @DisplayName("영화 찜이 이미 존재하면 찜 해제")
    void 영화찜_해제_성공() {
        // given
        Long userId = 1L;
        Long movieId = 100L;

        User user = User.builder()
                .nickname("jong")
                .email("jong@test.com")
                .birthdate(LocalDate.of(2000, 1, 28))
                .build();

        Movie movie = Movie.builder()
                .name("프로젝트 헤일메리")
                .runningTime(156)
                .ageRestriction(12)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));
        given(movieFavoriteRepository.existsByUserAndMovie(user, movie)).willReturn(true);

        // when
        FavoriteResponse response = favoriteService.toggleFavoriteMovie(userId, movieId);

        // then
        assertFalse(response.isFavorite());
    }

    @Test
    @DisplayName("영화 찜이 없으면 찜 추가")
    void 영화찜_성공() {
        // given
        Long userId = 1L;
        Long movieId = 100L;

        User user = User.builder().nickname("jong")
                        .email("jong@test.com")
                        .birthdate(LocalDate.of(2000, 1, 28))
                        .build();

        Movie movie = Movie.builder()
                        .name("프로젝트 헤일메리")
                        .runningTime(156)
                        .ageRestriction(12)
                        .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));
        given(movieFavoriteRepository.existsByUserAndMovie(user, movie)).willReturn(false);

        // when
        FavoriteResponse response = favoriteService.toggleFavoriteMovie(userId, movieId);

        // then
        assertTrue(response.isFavorite());
    }
}
