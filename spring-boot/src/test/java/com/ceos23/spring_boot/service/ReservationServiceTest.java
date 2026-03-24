package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.*;
import com.ceos23.spring_boot.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private SeatRepository seatRepository;

    @Test
    void 예매_성공() {
        // given
        Long userId = 1L;
        Long screeningId = 1L;
        Long seatId = 1L;

        User user = new User();
        Screening screening = new Screening();
        Seat seat = new Seat();

        given(reservationRepository.existsByScreeningIdAndSeatId(screeningId, seatId))
                .willReturn(false);

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(screeningRepository.findById(screeningId))
                .willReturn(Optional.of(screening));

        given(seatRepository.findById(seatId))
                .willReturn(Optional.of(seat));

        given(reservationRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Reservation result = reservationService.reserve(userId, screeningId, seatId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void 예매_실패_이미예약된좌석() {
        // given
        Long screeningId = 1L;
        Long seatId = 1L;

        given(reservationRepository.existsByScreeningIdAndSeatId(screeningId, seatId))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() ->
                reservationService.reserve(1L, screeningId, seatId)
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("이미 예약된 좌석입니다.");
    }
}