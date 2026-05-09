package com.ceos23.cgv.domain.reservation.service;

import com.ceos23.cgv.domain.cinema.entity.Cinema;
import com.ceos23.cgv.domain.cinema.entity.Theater;
import com.ceos23.cgv.domain.cinema.enums.TheaterType;
import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.entity.Screening;
import com.ceos23.cgv.domain.movie.repository.ScreeningRepository;
import com.ceos23.cgv.domain.reservation.dto.ReservationCreateRequest;
import com.ceos23.cgv.domain.reservation.dto.ReservationResponse;
import com.ceos23.cgv.domain.reservation.entity.Reservation;
import com.ceos23.cgv.domain.reservation.enums.Payment;
import com.ceos23.cgv.domain.reservation.repository.ReservationRepository;
import com.ceos23.cgv.domain.reservation.repository.ReservedSeatRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private ReservedSeatRepository reservedSeatRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("정상적으로 예매 정보가 생성되고 저장된다")
    void createReservation_Success() {
        // Given
        Long userId = 1L;
        Long screeningId = 1L;
        Payment payment = Payment.APP_CARD;
        String couponCode = null;
        ReservationCreateRequest request = new ReservationCreateRequest(
                screeningId,
                payment,
                couponCode,
                List.of("A1", "A2")
        );

        User user = User.builder().id(userId).name("우혁").nickname("우혁").build();

        Movie movie = Movie.builder().id(1L).title("테스트 영화").build();
        Cinema cinema = Cinema.builder().id(1L).name("CGV 신촌").build();
        Theater theater = Theater.builder().id(1L).name("1관").cinema(cinema).type(TheaterType.NORMAL).build();

        Screening screening = Screening.builder()
                .id(screeningId)
                .movie(movie)
                .theater(theater)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(screeningRepository.findById(screeningId)).willReturn(Optional.of(screening));

        // saveAndFlush 될 때 들어온 엔티티 그대로 반환
        given(reservationRepository.saveAndFlush(any(Reservation.class))).willAnswer(i -> i.getArgument(0));

        // When
        ReservationResponse response = reservationService.createReservation(userId, request);

        // Then
        assertThat(response.userName()).isEqualTo("우혁");
        assertThat(response.peopleCount()).isEqualTo(2);
        assertThat(response.payment()).isEqualTo(Payment.APP_CARD);
        verify(reservationRepository).saveAndFlush(any(Reservation.class));
    }

    @Test
    @DisplayName("존재하지 않는 상영일정으로 예매 시 SCREENING_NOT_FOUND 예외가 발생한다")
    void createReservation_Fail_ScreeningNotFound() {
        // Given
        Long userId = 1L;
        Long invalidScreeningId = 999L;
        ReservationCreateRequest request = new ReservationCreateRequest(
                invalidScreeningId,
                Payment.APP_CARD,
                null,
                List.of("A1", "A2")
        );

        User user = User.builder().id(userId).nickname("우혁").build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        // 상영 일정이 없다고 가정
        given(screeningRepository.findById(invalidScreeningId)).willReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reservationService.createReservation(userId, request);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SCREENING_NOT_FOUND);
    }
}
