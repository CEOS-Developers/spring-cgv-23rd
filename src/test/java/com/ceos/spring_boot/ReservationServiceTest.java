package com.ceos.spring_boot;

import com.ceos.spring_boot.domain.cinema.repository.SeatRepository;
import com.ceos.spring_boot.domain.reservation.entity.Reservation;
import com.ceos.spring_boot.domain.reservation.entity.ReservationStatus;
import com.ceos.spring_boot.domain.reservation.repository.ReservationRepository;
import com.ceos.spring_boot.domain.reservation.repository.ReservationSeatRepository;
import com.ceos.spring_boot.domain.reservation.service.ReservationService;
import com.ceos.spring_boot.domain.schedule.repository.ScheduleRepository;
import com.ceos.spring_boot.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private ReservationSeatRepository reservationSeatRepository;
    @Mock private UserRepository userRepository;
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private SeatRepository seatRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("예매 취소 성공 - 상태 변경 및 좌석 삭제")
    void cancelReservation_success() {
        // given
        Long reservationId = 1L;

        Reservation reservation = spy(Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.CONFIRMED)
                .build());

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // when
        reservationService.cancelReservation(reservationId);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
        verify(reservationSeatRepository, times(1)).deleteByReservationId(reservationId);
    }

    @Test
    @DisplayName("이미 취소된 예매를 다시 취소할 경우 예외 발생")
    void cancelReservation_fail_alreadyCanceled() {
        // given
        Long reservationId = 1L;
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.CANCELED)
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // when & then
        assertThatThrownBy(() -> reservationService.cancelReservation(reservationId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 취소된 예매입니다.");
    }
}
