package com.ceos23.spring_boot.cgv.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.cinema.Screen;
import com.ceos23.spring_boot.cgv.domain.cinema.ScreenType;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatLayout;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Movie;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.payment.PaymentLog;
import com.ceos23.spring_boot.cgv.domain.payment.PaymentStatus;
import com.ceos23.spring_boot.cgv.domain.reservation.Reservation;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationSeat;
import com.ceos23.spring_boot.cgv.domain.reservation.ReservationStatus;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.global.exception.ConflictException;
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
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReservationConcurrencyTest {

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
    @DisplayName("only one request can start payment for the same seat at the same time")
    void createReservation_allowsOnlyOneReservationForSameSeat() throws Exception {
        User user1 = userRepository.save(new User("user1", "user1@example.com", "password", UserRole.USER));
        User user2 = userRepository.save(new User("user2", "user2@example.com", "password", UserRole.USER));

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

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger conflictCount = new AtomicInteger();

        Future<Void> firstRequest = executorService.submit(
                reserveSeatTask(user1.getId(), screening.getId(), seatTemplate.getId(),
                        readyLatch, startLatch, successCount, conflictCount)
        );
        Future<Void> secondRequest = executorService.submit(
                reserveSeatTask(user2.getId(), screening.getId(), seatTemplate.getId(),
                        readyLatch, startLatch, successCount, conflictCount)
        );

        assertThat(readyLatch.await(5, TimeUnit.SECONDS)).isTrue();
        startLatch.countDown();

        waitFor(firstRequest);
        waitFor(secondRequest);
        executorService.shutdown();

        List<ReservationSeat> reservationSeats = reservationSeatRepository.findAll();
        List<Reservation> reservations = reservationRepository.findAll();
        List<PaymentLog> paymentLogs = paymentLogRepository.findAll();
        List<Long> activeSeatIds = reservationSeatRepository.findActiveSeatTemplateIdsByScreeningAndSeatTemplates(
                screening,
                List.of(seatTemplate),
                ReservationStatus.CONFIRMED,
                ReservationStatus.PENDING_PAYMENT,
                LocalDateTime.now()
        );

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(conflictCount.get()).isEqualTo(1);
        assertThat(reservationSeats).hasSize(1);
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
        assertThat(paymentLogs).hasSize(1);
        assertThat(paymentLogs.get(0).getStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(activeSeatIds).containsExactly(seatTemplate.getId());
    }

    private Callable<Void> reserveSeatTask(
            Long userId,
            Long screeningId,
            Long seatTemplateId,
            CountDownLatch readyLatch,
            CountDownLatch startLatch,
            AtomicInteger successCount,
            AtomicInteger conflictCount
    ) {
        return () -> {
            readyLatch.countDown();
            assertThat(startLatch.await(5, TimeUnit.SECONDS)).isTrue();

            try {
                reservationService.createReservation(userId, screeningId, List.of(seatTemplateId));
                successCount.incrementAndGet();
            } catch (ConflictException exception) {
                conflictCount.incrementAndGet();
            }

            return null;
        };
    }

    private void waitFor(Future<Void> future) throws Exception {
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException exception) {
            throw new RuntimeException(exception.getCause());
        }
    }
}
