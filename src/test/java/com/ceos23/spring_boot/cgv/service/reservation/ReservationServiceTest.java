package com.ceos23.spring_boot.cgv.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.cinema.Screen;
import com.ceos23.spring_boot.cgv.domain.cinema.ScreenType;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatLayout;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Movie;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationStatus;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.global.exception.BadRequestException;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.cinema.SeatTemplateRepository;
import com.ceos23.spring_boot.cgv.repository.movie.ScreeningRepository;
import com.ceos23.spring_boot.cgv.repository.reservation.ReservationRepository;
import com.ceos23.spring_boot.cgv.repository.reservation.ReservationSeatRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private SeatTemplateRepository seatTemplateRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationSeatRepository reservationSeatRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private Screening screening;
    private SeatTemplate seatTemplate1;
    private SeatTemplate seatTemplate2;

    @BeforeEach
    void setUp() {
        user = new User("지송", "jisong@example.com", "encoded-password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Cinema cinema = new Cinema("CGV 강남", "서울시 강남구");
        ReflectionTestUtils.setField(cinema, "id", 1L);

        SeatLayout seatLayout = new SeatLayout("일반관 기본 좌석", 3, 3);
        ReflectionTestUtils.setField(seatLayout, "id", 1L);

        Screen screen = new Screen("1관", ScreenType.GENERAL, cinema, seatLayout);
        ReflectionTestUtils.setField(screen, "id", 1L);

        Movie movie = new Movie("괴물", 120, "12세 이상 관람가", "한국 영화");
        ReflectionTestUtils.setField(movie, "id", 1L);

        screening = new Screening(
                LocalDateTime.of(2026, 3, 20, 19, 0),
                LocalDateTime.of(2026, 3, 20, 21, 49),
                movie,
                screen
        );
        ReflectionTestUtils.setField(screening, "id", 1L);

        seatTemplate1 = new SeatTemplate("A", 1, seatLayout);
        ReflectionTestUtils.setField(seatTemplate1, "id", 1L);

        seatTemplate2 = new SeatTemplate("A", 2, seatLayout);
        ReflectionTestUtils.setField(seatTemplate2, "id", 2L);
    }

    @Test
    @DisplayName("예매 생성 성공")
    void createReservation_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(screeningRepository.findById(1L)).willReturn(Optional.of(screening));
        given(seatTemplateRepository.findAllById(List.of(1L, 2L)))
                .willReturn(List.of(seatTemplate1, seatTemplate2));
        given(reservationSeatRepository.findReservedSeatTemplateIdsByScreeningAndSeatTemplates(
                screening,
                List.of(seatTemplate1, seatTemplate2),
                ReservationStatus.RESERVED
        )).willReturn(List.of());
        given(reservationRepository.save(any(Reservation.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Reservation result = reservationService.createReservation(1L, 1L, List.of(1L, 2L));

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getScreening()).isEqualTo(screening);
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.RESERVED);

        then(reservationRepository).should().save(any(Reservation.class));
        then(reservationSeatRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("예매 생성 실패 - 좌석 요청이 비어 있음")
    void createReservation_fail_emptySeats() {
        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("예매 생성 실패 - 사용자 없음")
    void createReservation_fail_userNotFound() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(1L)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("예매 생성 실패 - 상영 정보 없음")
    void createReservation_fail_screeningNotFound() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(screeningRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(1L)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("예매 생성 실패 - 요청 좌석 중복")
    void createReservation_fail_duplicateSeatRequest() {
        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(1L, 1L)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("예매 생성 실패 - 이미 예매된 좌석")
    void createReservation_fail_alreadyReservedSeat() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(screeningRepository.findById(1L)).willReturn(Optional.of(screening));
        given(seatTemplateRepository.findAllById(List.of(1L)))
                .willReturn(List.of(seatTemplate1));
        given(reservationSeatRepository.findReservedSeatTemplateIdsByScreeningAndSeatTemplates(
                screening,
                List.of(seatTemplate1),
                ReservationStatus.RESERVED
        )).willReturn(List.of(1L));

        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(1L)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("예매 생성 실패 - 상영관에 없는 좌석")
    void createReservation_fail_invalidSeatForScreening() {
        SeatLayout otherSeatLayout = new SeatLayout("특별관 좌석", 2, 2);
        ReflectionTestUtils.setField(otherSeatLayout, "id", 2L);
        SeatTemplate invalidSeat = new SeatTemplate("B", 1, otherSeatLayout);
        ReflectionTestUtils.setField(invalidSeat, "id", 3L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(screeningRepository.findById(1L)).willReturn(Optional.of(screening));
        given(seatTemplateRepository.findAllById(List.of(3L)))
                .willReturn(List.of(invalidSeat));

        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(3L)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("예매 목록 조회는 사용자 기준 repository query를 사용한다")
    void getReservations_success() {
        Reservation reservation = new Reservation(user, screening);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(reservationRepository.findAllByUserId(1L)).willReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservations(1L);

        assertThat(reservations).containsExactly(reservation);
        then(reservationRepository).should().findAllByUserId(1L);
    }

    @Test
    @DisplayName("예매 취소 성공")
    void cancelReservation_success() {
        Reservation reservation = new Reservation(user, screening);
        given(reservationRepository.findById(1L)).willReturn(Optional.of(reservation));

        reservationService.cancelReservation(1L);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    @DisplayName("예매 취소 실패 - 이미 취소된 예매")
    void cancelReservation_fail_alreadyCanceled() {
        Reservation reservation = new Reservation(user, screening);
        reservation.cancel();
        given(reservationRepository.findById(1L)).willReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(1L))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("예매 취소 실패 - 존재하지 않는 예매")
    void cancelReservation_fail_notFound() {
        given(reservationRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancelReservation(1L))
                .isInstanceOf(NotFoundException.class);
    }
}
