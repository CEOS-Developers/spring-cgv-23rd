package com.ceos23.spring_boot.cgv.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.cinema.Screen;
import com.ceos23.spring_boot.cgv.domain.cinema.ScreenType;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatLayout;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Movie;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.payment.PaymentStatus;
import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationStatus;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.cinema.CinemaRepository;
import com.ceos23.spring_boot.cgv.repository.cinema.ScreenRepository;
import com.ceos23.spring_boot.cgv.repository.cinema.SeatLayoutRepository;
import com.ceos23.spring_boot.cgv.repository.cinema.SeatTemplateRepository;
import com.ceos23.spring_boot.cgv.repository.movie.MovieRepository;
import com.ceos23.spring_boot.cgv.repository.movie.ScreeningRepository;
import com.ceos23.spring_boot.cgv.repository.payment.PaymentLogRepository;
import com.ceos23.spring_boot.cgv.repository.reservation.ReservationRepository;
import com.ceos23.spring_boot.cgv.repository.reservation.ReservationSeatRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import com.ceos23.spring_boot.cgv.service.payment.PaymentService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class ReservationPaymentFlowTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private SeatLayoutRepository seatLayoutRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private SeatTemplateRepository seatTemplateRepository;

    @Autowired
    private ReservationSeatRepository reservationSeatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentLogRepository paymentLogRepository;

    @AfterEach
    void tearDown() {
        reservationSeatRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        paymentLogRepository.deleteAllInBatch();
        screeningRepository.deleteAllInBatch();
        seatTemplateRepository.deleteAllInBatch();
        screenRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
        seatLayoutRepository.deleteAllInBatch();
        cinemaRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("reservation hold becomes confirmed after payment and releases seat on cancel")
    void reservationFlow_confirmPaymentAndCancel() {
        Fixture fixture = createFixture();

        Reservation reservation = reservationService.createReservation(
                fixture.user.getId(),
                fixture.screening.getId(),
                List.of(fixture.seatTemplate.getId())
        );

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
        assertThat(reservation.getExpiresAt()).isNotNull();
        assertThat(paymentService.getPayment(reservation.getPaymentId(), fixture.user.getId()).getStatus())
                .isEqualTo(PaymentStatus.READY);
        assertThat(reservationSeatRepository.findAllByReservation(reservation)).hasSize(1);

        Reservation confirmedReservation = reservationService.confirmPayment(reservation.getId(), fixture.user.getId());

        assertThat(confirmedReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(confirmedReservation.getExpiresAt()).isNull();
        assertThat(paymentService.getPayment(reservation.getPaymentId(), fixture.user.getId()).getStatus())
                .isEqualTo(PaymentStatus.PAID);
        assertThat(reservationSeatRepository.findAllByReservation(confirmedReservation)).hasSize(1);

        reservationService.cancelReservation(reservation.getId(), fixture.user.getId());

        assertThat(reservationRepository.findById(reservation.getId()))
                .get()
                .extracting("status")
                .isEqualTo(ReservationStatus.CANCELED);
        assertThat(paymentService.getPayment(reservation.getPaymentId(), fixture.user.getId()).getStatus())
                .isEqualTo(PaymentStatus.CANCELLED);
        assertThat(reservationSeatRepository.findAllByReservation(reservation)).isEmpty();
    }

    @Test
    @DisplayName("expired pending reservation releases the seat and allows a new reservation")
    void expiredPendingReservation_releasesSeat() {
        Fixture fixture = createFixture();
        User anotherUser = userRepository.save(new User("user2", "user2@example.com", "password", UserRole.USER));

        Reservation reservation = reservationService.createReservation(
                fixture.user.getId(),
                fixture.screening.getId(),
                List.of(fixture.seatTemplate.getId())
        );

        ReflectionTestUtils.setField(reservation, "expiresAt", LocalDateTime.now().minusMinutes(1));
        reservationRepository.saveAndFlush(reservation);

        reservationService.expireOverdueReservations();

        Reservation expiredReservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(expiredReservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(paymentService.getPayment(reservation.getPaymentId(), fixture.user.getId()).getStatus())
                .isEqualTo(PaymentStatus.EXPIRED);
        assertThat(reservationSeatRepository.findAllByReservation(expiredReservation)).isEmpty();

        Reservation newReservation = reservationService.createReservation(
                anotherUser.getId(),
                fixture.screening.getId(),
                List.of(fixture.seatTemplate.getId())
        );

        assertThat(newReservation.getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
        assertThat(reservationSeatRepository.findAllByReservation(newReservation)).hasSize(1);
    }

    @Test
    @DisplayName("payment lookup fails when payment belongs to another user")
    void getPayment_fail_whenUserDoesNotOwnPayment() {
        Fixture fixture = createFixture();
        User otherUser = userRepository.save(new User("other", "other@example.com", "password", UserRole.USER));

        Reservation reservation = reservationService.createReservation(
                fixture.user.getId(),
                fixture.screening.getId(),
                List.of(fixture.seatTemplate.getId())
        );

        assertThat(paymentService.getPayment(reservation.getPaymentId(), fixture.user.getId()).getStatus())
                .isEqualTo(PaymentStatus.READY);

        assertThatThrownBy(() -> paymentService.getPayment(reservation.getPaymentId(), otherUser.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("payment cannot be completed after reservation expiration")
    void confirmPayment_fail_whenReservationExpired() {
        Fixture fixture = createFixture();

        Reservation reservation = reservationService.createReservation(
                fixture.user.getId(),
                fixture.screening.getId(),
                List.of(fixture.seatTemplate.getId())
        );

        ReflectionTestUtils.setField(reservation, "expiresAt", LocalDateTime.now().minusMinutes(1));
        reservationRepository.saveAndFlush(reservation);

        assertThatThrownBy(() -> reservationService.confirmPayment(reservation.getId(), fixture.user.getId()))
                .isInstanceOf(ConflictException.class);
    }

    private Fixture createFixture() {
        User user = userRepository.save(new User("user1", "user1@example.com", "password", UserRole.USER));
        Cinema cinema = cinemaRepository.save(new Cinema("CGV Gangnam", "Seoul"));
        SeatLayout seatLayout = seatLayoutRepository.save(new SeatLayout("General", 3, 3));
        Screen screen = screenRepository.save(new Screen("1", ScreenType.GENERAL, cinema, seatLayout));
        Movie movie = movieRepository.save(new Movie("Movie", 120, "12", "Description"));
        Screening screening = screeningRepository.save(new Screening(
                LocalDateTime.of(2026, 4, 25, 19, 0),
                LocalDateTime.of(2026, 4, 25, 21, 0),
                movie,
                screen
        ));
        SeatTemplate seatTemplate = seatTemplateRepository.save(new SeatTemplate("A", 1, seatLayout));

        return new Fixture(user, screening, seatTemplate);
    }

    private record Fixture(User user, Screening screening, SeatTemplate seatTemplate) {
    }
}
