package com.ceos23.cgv.domain.cinema.service;

import com.ceos23.cgv.domain.cinema.dto.ReviewCreateRequest;
import com.ceos23.cgv.domain.cinema.entity.Review;
import com.ceos23.cgv.domain.cinema.enums.TheaterType;
import com.ceos23.cgv.domain.cinema.repository.ReviewRepository;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.repository.MovieRepository;
import com.ceos23.cgv.domain.user.entity.User;
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
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("관람평(Review) 작성 성공 테스트")
    void createReview_Success() {
        // Given
        ReviewCreateRequest request = new ReviewCreateRequest(1L, 1L, TheaterType.IMAX, "IMAX로 보니 작화가 엄청나네요!");
        User user = User.builder().id(1L).nickname("우혁").build();
        Movie movie = Movie.builder().id(1L).title("원피스 필름 레드").build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(movieRepository.findById(1L)).willReturn(Optional.of(movie));
        given(reviewRepository.save(any(Review.class))).willAnswer(i -> i.getArgument(0));

        // When
        Review savedReview = reviewService.createReview(request);

        // Then
        assertThat(savedReview.getUser().getNickname()).isEqualTo("우혁");
        assertThat(savedReview.getMovie().getTitle()).isEqualTo("원피스 필름 레드");
        assertThat(savedReview.getContent()).isEqualTo("IMAX로 보니 작화가 엄청나네요!");
        assertThat(savedReview.getType()).isEqualTo(TheaterType.IMAX);
        assertThat(savedReview.getLikeCount()).isEqualTo(0); // 기본값 확인

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("존재하지 않는 영화에 관람평 작성 시 MOVIE_NOT_FOUND 예외 발생")
    void createReview_Fail_MovieNotFound() {
        // Given
        ReviewCreateRequest request = new ReviewCreateRequest(1L, 999L, TheaterType.NORMAL, "테스트 리뷰");
        User user = User.builder().id(1L).nickname("우혁").build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        // 영화 조회가 안 되는 상황 가정
        given(movieRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.createReview(request);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MOVIE_NOT_FOUND);
    }
}