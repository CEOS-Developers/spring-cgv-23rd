package com.ceos23.cgv.domain.user.service;

import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.domain.cinema.repository.CinemaRepository;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.repository.MovieRepository;
import com.ceos23.cgv.domain.user.entity.Cinetalk;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.domain.user.repository.CinetalkRepository;
import com.ceos23.cgv.domain.user.repository.UserRepository;
import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CinetalkServiceTest {

    @Mock
    private CinetalkRepository cinetalkRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private CinemaRepository cinemaRepository;

    @InjectMocks
    private CinetalkService cinetalkService;

    @Test
    @DisplayName("씨네톡 작성 성공 테스트")
    void createCinetalk_Success() {
        // Given (준비)
        Long userId = 1L;
        Long movieId = 1L;
        String content = "정말 재밌는 영화였어요!";

        User user = User.builder().id(userId).nickname("우혁").build();
        Movie movie = Movie.builder().id(movieId).title("아바타").build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));

        given(cinetalkRepository.save(any(Cinetalk.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When (실행)
        Cinetalk savedCinetalk = cinetalkService.createCinetalk(userId, content, movieId, null);

        // Then (검증)
        assertThat(savedCinetalk.getUser().getNickname()).isEqualTo("우혁");
        assertThat(savedCinetalk.getContent()).isEqualTo(content);
        assertThat(savedCinetalk.getMovie().getTitle()).isEqualTo("아바타");
        assertThat(savedCinetalk.getLikeCount()).isEqualTo(0); // 초기 좋아요 수는 0이어야 함

        // cinetalkRepository.save가 실제로 1번 호출되었는지 검증
        verify(cinetalkRepository).save(any(Cinetalk.class));
    }

    @Test
    @DisplayName("존재하지 않는 유저로 씨네톡 작성 시 CustomException(USER_NOT_FOUND) 발생")
    void createCinetalk_Fail_UserNotFound() {
        // Given (준비)
        Long invalidUserId = 999L;

        // DB에 유저가 없는 상황을 가정 (Optional.empty 반환)
        given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

        // When (실행) & Then (검증)
        CustomException exception = assertThrows(CustomException.class, () -> {
            cinetalkService.createCinetalk(invalidUserId, "테스트 내용", 1L, null);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}