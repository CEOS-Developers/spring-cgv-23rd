package com.ceos23.cgv_clone.service;

import com.ceos23.cgv_clone.common.ApiResponse;
import com.ceos23.cgv_clone.movie.domain.Movie;
import com.ceos23.cgv_clone.movie.domain.Schedule;
import com.ceos23.cgv_clone.movie.repository.ScheduleRepository;
import com.ceos23.cgv_clone.reservation.domain.Reservation;
import com.ceos23.cgv_clone.reservation.domain.ReservationStatus;
import com.ceos23.cgv_clone.reservation.dto.request.ReservationRequest;
import com.ceos23.cgv_clone.reservation.dto.response.ReservationResponse;
import com.ceos23.cgv_clone.reservation.repository.ReservationRepository;
import com.ceos23.cgv_clone.reservation.repository.ReservationSeatRepository;
import com.ceos23.cgv_clone.reservation.service.ReservationService;
import com.ceos23.cgv_clone.theater.domain.Screen;
import com.ceos23.cgv_clone.theater.domain.ScreenType;
import com.ceos23.cgv_clone.theater.domain.ScreenTypeCode;
import com.ceos23.cgv_clone.theater.domain.Theater;
import com.ceos23.cgv_clone.user.domain.User;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationSeatRepository reservationSeatRepository;

    @Test
    @DisplayName("예매 생성 성공")
    void 예매생성_성공() {
        // given
        Long userId = 1L;
        Long scheduleId = 10L;

        User user = User.builder()
                .nickname("jong")
                .email("jong@test.com")
                .birthdate(LocalDate.of(2000, 1, 28))
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Movie movie = Movie.builder()
                .name("프로젝트 헤일메리")
                .runningTime(156)
                .ageRestriction(12)
                .build();

        ScreenType screenType = ScreenType.builder()
                .screenTypeCode(ScreenTypeCode.STD)
                .price(15000)
                .maxRow('H')
                .maxCol(10)
                .build();

        Theater theater = Theater.builder()
                .name("CGV 강남")
                .region("서울")
                .address("서울 강남구")
                .build();

        Screen screen = Screen.builder()
                .name("1관")
                .theater(theater)
                .screenType(screenType)
                .build();

        Schedule schedule = Schedule.builder()
                .startAt(LocalDateTime.of(2026, 3, 22, 14, 0))
                .endAt(LocalDateTime.of(2026, 3, 22, 16, 36))
                .screen(screen)
                .movie(movie)
                .build();
        ReflectionTestUtils.setField(schedule, "id", scheduleId);

        ReservationRequest request = new ReservationRequest();
        ReflectionTestUtils.setField(request, "scheduleId", scheduleId);
        ReflectionTestUtils.setField(request, "seats", List.of("A1", "A2"));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(scheduleRepository.findById(scheduleId)).willReturn(Optional.of(schedule));
        given(reservationSeatRepository.existsByScheduleAndSeatRowAndSeatCol(schedule, 'A', 1)).willReturn(false);
        given(reservationSeatRepository.existsByScheduleAndSeatRowAndSeatCol(schedule, 'A', 2)).willReturn(false);

        // when
        ApiResponse<ReservationResponse> response = reservationService.createReservation(userId, request);

        // then
        assertTrue(response.isSuccess());
        assertEquals(201, response.getResultCode());
        assertEquals("INSERT SUCCESS", response.getResultMsg());
        assertNotNull(response.getResult());

        ReservationResponse result = response.getResult();
        assertEquals(30000, result.getTotalPrice());
        assertEquals(ReservationStatus.RESERVED, result.getReservationStatus());
        assertEquals("프로젝트 헤일메리", result.getMovieName());
        assertEquals("CGV 강남", result.getTheaterName());
        assertEquals("1관", result.getScreenName());
        assertEquals(List.of("A1", "A2"), result.getReservedSeats());
    }

    @Test
    @DisplayName("예매 취소 성공")
    void 예매취소_성공() {
        // given
        Long userId = 1L;
        Long reservationId = 100L;

        User user = User.builder()
                .nickname("jong")
                .email("jong@test.com")
                .birthdate(LocalDate.of(2000, 1, 28))
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Reservation reservation = Reservation.builder()
                .reservedAt(LocalDateTime.now())
                .totalPrice(15000)
                .status(ReservationStatus.RESERVED)
                .user(user)
                .schedule(null)
                .seatNames("A1")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

        // when
        ApiResponse<Void> response = reservationService.cancelReservation(userId, reservationId);

        // then
        assertTrue(response.isSuccess());
        assertEquals(200, response.getResultCode());
        assertEquals("DELETE SUCCESS", response.getResultMsg());
        assertEquals(ReservationStatus.CANCELED, reservation.getStatus());
    }
}
