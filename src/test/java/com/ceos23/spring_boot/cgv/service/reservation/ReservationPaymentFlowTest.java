package com.ceos23.spring_boot.cgv.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReservationPaymentFlowTest {

    @Autowired
    private ReservationService reservationService;

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
    @DisplayName("reservation create and cancel update payment status together")
    void reservationCreateAndCancel_updatesPaymentStatus() {
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

        Reservation reservation = reservationService.createReservation(
                user.getId(),
                screening.getId(),
                List.of(seatTemplate.getId()),
                "payment-flow-001"
        );

        assertThat(reservation.getPaymentId()).isEqualTo("payment-flow-001");
        assertThat(paymentLogRepository.findByPaymentId("payment-flow-001"))
                .get()
                .extracting("status")
                .isEqualTo(PaymentStatus.PAID);

        reservationService.cancelReservation(reservation.getId());

        assertThat(paymentLogRepository.findByPaymentId("payment-flow-001"))
                .get()
                .extracting("status")
                .isEqualTo(PaymentStatus.CANCELLED);
        assertThat(reservationRepository.findById(reservation.getId()))
                .get()
                .extracting("status")
                .isEqualTo(ReservationStatus.CANCELED);
    }
}
