package com.ceos23.spring_boot.cgv.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.cinema.Screen;
import com.ceos23.spring_boot.cgv.domain.cinema.ScreenType;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatLayout;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Movie;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.payment.PaymentLog;
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
import com.ceos23.spring_boot.cgv.service.payment.PaymentCreateCommand;
import com.ceos23.spring_boot.cgv.service.payment.PaymentService;
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

    private static final String PAYMENT_ID = "pay-001";

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

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private Screening screening;
    private SeatTemplate seatTemplate1;
    private SeatTemplate seatTemplate2;

    @BeforeEach
    void setUp() {
        user = new User("jisong", "jisong@example.com", "encoded-password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Cinema cinema = new Cinema("CGV Gangnam", "Seoul");
        ReflectionTestUtils.setField(cinema, "id", 1L);

        SeatLayout seatLayout = new SeatLayout("General", 3, 3);
        ReflectionTestUtils.setField(seatLayout, "id", 1L);

        Screen screen = new Screen("1", ScreenType.GENERAL, cinema, seatLayout);
        ReflectionTestUtils.setField(screen, "id", 1L);

        Movie movie = new Movie("Movie", 120, "12", "Description");
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

        lenient().when(reservationRepository.findAllByStatusAndExpiresAtBefore(
                eq(ReservationStatus.PENDING_PAYMENT),
                any(LocalDateTime.class)
        )).thenReturn(List.of());
    }

    @Test
    @DisplayName("create reservation success creates pending payment hold")
    void createReservation_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(screeningRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(screening));
        given(seatTemplateRepository.findAllById(List.of(1L, 2L)))
                .willReturn(List.of(seatTemplate1, seatTemplate2));
        given(reservationSeatRepository.findActiveSeatTemplateIdsByScreeningAndSeatTemplates(
                eq(screening),
                eq(List.of(seatTemplate1, seatTemplate2)),
                eq(ReservationStatus.CONFIRMED),
                eq(ReservationStatus.PENDING_PAYMENT),
                any(LocalDateTime.class)
        )).willReturn(List.of());
        given(paymentService.startPayment(any(PaymentCreateCommand.class)))
                .willReturn(new PaymentLog(PAYMENT_ID, "Movie reservation", 28_000L, "screeningId=1"));
        given(reservationRepository.save(any(Reservation.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Reservation result = reservationService.createReservation(1L, 1L, List.of(1L, 2L));

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getScreening()).isEqualTo(screening);
        assertThat(result.getPaymentId()).isNotBlank();
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
        assertThat(result.getExpiresAt()).isAfter(result.getReservedAt());

        then(paymentService).should().startPayment(any(PaymentCreateCommand.class));
        then(reservationRepository).should().save(any(Reservation.class));
        then(reservationSeatRepository).should().saveAllAndFlush(anyList());
    }

    @Test
    @DisplayName("create reservation fails when seats are empty")
    void createReservation_fail_emptySeats() {
        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of()))
                .isInstanceOf(BadRequestException.class);
        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("create reservation fails when user is missing")
    void createReservation_fail_userNotFound() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(1L)))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("create reservation fails when screening is missing")
    void createReservation_fail_screeningNotFound() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(screeningRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(1L)))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("create reservation fails when seat request is duplicated")
    void createReservation_fail_duplicateSeatRequest() {
        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(1L, 1L)))
                .isInstanceOf(BadRequestException.class);
        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("create reservation fails when seat is already reserved")
    void createReservation_fail_alreadyReservedSeat() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(screeningRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(screening));
        given(seatTemplateRepository.findAllById(List.of(1L)))
                .willReturn(List.of(seatTemplate1));
        given(reservationSeatRepository.findActiveSeatTemplateIdsByScreeningAndSeatTemplates(
                eq(screening),
                eq(List.of(seatTemplate1)),
                eq(ReservationStatus.CONFIRMED),
                eq(ReservationStatus.PENDING_PAYMENT),
                any(LocalDateTime.class)
        )).willReturn(List.of(1L));

        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(1L)))
                .isInstanceOf(ConflictException.class);
        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("create reservation fails when seat does not belong to screening")
    void createReservation_fail_invalidSeatForScreening() {
        SeatLayout otherSeatLayout = new SeatLayout("Special", 2, 2);
        ReflectionTestUtils.setField(otherSeatLayout, "id", 2L);
        SeatTemplate invalidSeat = new SeatTemplate("B", 1, otherSeatLayout);
        ReflectionTestUtils.setField(invalidSeat, "id", 3L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(screeningRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(screening));
        given(seatTemplateRepository.findAllById(List.of(3L)))
                .willReturn(List.of(invalidSeat));

        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(3L)))
                .isInstanceOf(BadRequestException.class);
        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("create reservation fails when payment hold creation fails")
    void createReservation_fail_paymentStartConflict() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(screeningRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(screening));
        given(seatTemplateRepository.findAllById(List.of(1L)))
                .willReturn(List.of(seatTemplate1));
        given(reservationSeatRepository.findActiveSeatTemplateIdsByScreeningAndSeatTemplates(
                eq(screening),
                eq(List.of(seatTemplate1)),
                eq(ReservationStatus.CONFIRMED),
                eq(ReservationStatus.PENDING_PAYMENT),
                any(LocalDateTime.class)
        )).willReturn(List.of());
        given(paymentService.startPayment(any(PaymentCreateCommand.class)))
                .willThrow(new ConflictException(com.ceos23.spring_boot.cgv.global.exception.ErrorCode.DUPLICATE_PAYMENT_ID));
        given(reservationRepository.save(any(Reservation.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> reservationService.createReservation(1L, 1L, List.of(1L)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("get reservations uses repository query by user id")
    void getReservations_success() {
        Reservation reservation = new Reservation(user, screening, PAYMENT_ID, LocalDateTime.now().plusMinutes(5));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(reservationRepository.findAllByUserId(1L)).willReturn(List.of(reservation));

        List<Reservation> reservations = reservationService.getReservations(1L);

        assertThat(reservations).containsExactly(reservation);
        then(reservationRepository).should().findAllByUserId(1L);
    }

    @Test
    @DisplayName("confirm payment success")
    void confirmPayment_success() {
        Reservation reservation = new Reservation(user, screening, PAYMENT_ID, LocalDateTime.now().plusMinutes(5));
        PaymentLog paymentLog = new PaymentLog(PAYMENT_ID, "Movie reservation", 14_000L, "screeningId=1");
        paymentLog.complete();
        given(reservationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(reservation));
        given(paymentService.completePayment(PAYMENT_ID)).willReturn(paymentLog);

        Reservation result = reservationService.confirmPayment(1L, 1L);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(result.getExpiresAt()).isNull();
        then(paymentService).should().completePayment(PAYMENT_ID);
    }

    @Test
    @DisplayName("confirm payment fails when payment window is expired")
    void confirmPayment_fail_expired() {
        Reservation reservation = new Reservation(user, screening, PAYMENT_ID, LocalDateTime.now().minusMinutes(1));
        given(reservationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.confirmPayment(1L, 1L))
                .isInstanceOf(ConflictException.class);

        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("cancel reservation success releases seat hold")
    void cancelReservation_success() {
        Reservation reservation = new Reservation(user, screening, PAYMENT_ID, LocalDateTime.now().plusMinutes(5));
        reservation.confirmPayment(LocalDateTime.now());
        PaymentLog paymentLog = new PaymentLog(PAYMENT_ID, "Movie reservation", 14_000L, "screeningId=1");
        paymentLog.complete();
        given(reservationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(reservation));
        given(paymentService.cancelPayment(PAYMENT_ID)).willReturn(paymentLog);

        reservationService.cancelReservation(1L, 1L);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
        then(paymentService).should().cancelPayment(PAYMENT_ID);
        then(reservationSeatRepository).should().deleteAllByReservation(reservation);
        then(reservationSeatRepository).should().flush();
    }

    @Test
    @DisplayName("cancel reservation fails when already canceled")
    void cancelReservation_fail_alreadyCanceled() {
        Reservation reservation = new Reservation(user, screening, PAYMENT_ID, LocalDateTime.now().plusMinutes(5));
        reservation.cancel(LocalDateTime.now());
        given(reservationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(1L, 1L))
                .isInstanceOf(ConflictException.class);

        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("cancel reservation fails when reservation is missing")
    void cancelReservation_fail_notFound() {
        given(reservationRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancelReservation(1L, 1L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("cancel reservation fails when reservation belongs to another user")
    void cancelReservation_fail_forbiddenUser() {
        given(reservationRepository.findByIdAndUserId(1L, 2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancelReservation(1L, 2L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(paymentService);
    }
}
