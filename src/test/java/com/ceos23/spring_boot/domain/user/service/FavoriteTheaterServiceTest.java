package com.ceos23.spring_boot.domain.user.service;

import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.domain.theater.repository.TheaterRepository;
import com.ceos23.spring_boot.domain.user.dto.FavoriteTheaterInfo;
import com.ceos23.spring_boot.domain.user.entity.FavoriteTheater;
import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.domain.user.repository.FavoriteTheaterRepository;
import com.ceos23.spring_boot.domain.user.repository.UserRepository;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FavoriteTheaterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private FavoriteTheaterRepository favoriteTheaterRepository;

    @InjectMocks
    private FavoriteTheaterService favoriteTheaterService;

    @Test
    @DisplayName("찜 성공: 기존에 찜하지 않았으면, 새로 저장하고 true를 반환한다.")
    void toggleFavorite_Success_AddFavorite() {
        // Given
        String userEmail = "user@naver.com";
        Long theaterId = 1L;

        User user = User.builder().build();
        Theater theater = Theater.builder()
                .name("CGV 강남")
                .location("서울")
                .build();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));
        given(theaterRepository.findById(theaterId)).willReturn(Optional.of(theater));
        given(favoriteTheaterRepository.findByUserAndTheater(user, theater)).willReturn(Optional.empty());

        // When
        boolean isFavorited = favoriteTheaterService.toggleFavorite(userEmail, theaterId);

        // Then
        assertThat(isFavorited).isTrue();
        verify(favoriteTheaterRepository).save(any(FavoriteTheater.class));
        verify(favoriteTheaterRepository, never()).delete(any());
    }

    @Test
    @DisplayName("찜 해제 성공: 기존에 찜 했으면 삭제하고 false를 반환한다.")
    void toggleFavorite_Success_RemoveFavorite() {
        // Given
        String userEmail = "user@naver.com";
        Long theaterId = 1L;

        User user = User.builder().build();
        Theater theater = Theater.builder()
                .name("CGV 강남")
                .location("서울")
                .build();
        FavoriteTheater favoriteTheater = new FavoriteTheater(user, theater);

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));
        given(theaterRepository.findById(theaterId)).willReturn(Optional.of(theater));
        given(favoriteTheaterRepository.findByUserAndTheater(user, theater)).willReturn(Optional.of(favoriteTheater));

        // When
        boolean isFavorited = favoriteTheaterService.toggleFavorite(userEmail, theaterId);

        // Then
        assertThat(isFavorited).isFalse();
        verify(favoriteTheaterRepository).delete(favoriteTheater);
        verify(favoriteTheaterRepository, never()).save(any());
    }

    @Test
    @DisplayName("찜 토글 실패: 존재하지 않는 회원일 경우 예외가 발생한다.")
    void toggleFavorite_Fail_UserNotFound() {
        // Given
        String userEmail = "user@naver.com";
        Long theaterId = 1L;

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> favoriteTheaterService.toggleFavorite(userEmail, theaterId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("찜 목록 조회 성공: 유저가 찜한 영화관 목록을 DTO로 변환하여 반환한다.")
    void getFavoriteTheaters_Success() {
        // Given
        String userEmail = "user@naver.com";

        User user = User.builder().build();
        Theater theater1 = Theater.builder()
                .name("CGV 강남")
                .location("서울")
                .build();
        ReflectionTestUtils.setField(theater1, "id", 1L);

        Theater theater2 = Theater.builder()
                .name("CGV 판교")
                .location("경기")
                .build();
        ReflectionTestUtils.setField(theater2, "id", 2L);

        FavoriteTheater favorite1 = new FavoriteTheater(user, theater1);
        FavoriteTheater favorite2 = new FavoriteTheater(user, theater2);

        given(userRepository.existsByEmail(userEmail)).willReturn(true);
        given(favoriteTheaterRepository.findAllByUserEmail(userEmail)).willReturn(List.of(favorite1, favorite2));

        // When
        List<FavoriteTheaterInfo> result = favoriteTheaterService.findFavoriteTheaters(userEmail);

        // Then
        assertThat(result).hasSize(2);

        assertThat(result.get(0).theater().getId()).isEqualTo(1L);
        assertThat(result.get(0).theater().getName()).isEqualTo("CGV 강남");

        assertThat(result.get(1).theater().getId()).isEqualTo(2L);
        assertThat(result.get(1).theater().getName()).isEqualTo("CGV 판교");
    }

    @Test
    @DisplayName("찜 목록 조회 실패: 존재하지 않는 회원일 경우 예외가 발생한다.")
    void getFavoriteTheaters_Fail_UserNotFound() {
        // Given
        String userEmail = "user@naver.com";
        given(userRepository.existsByEmail(userEmail)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> favoriteTheaterService.findFavoriteTheaters(userEmail))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}