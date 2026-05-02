package com.ceos23.spring_boot.cgv.service.screening;

import static org.assertj.core.api.Assertions.assertThat;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.cinema.Screen;
import com.ceos23.spring_boot.cgv.domain.cinema.ScreenType;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatLayout;
import com.ceos23.spring_boot.cgv.domain.cinema.SeatTemplate;
import com.ceos23.spring_boot.cgv.domain.movie.Movie;
import com.ceos23.spring_boot.cgv.domain.movie.Screening;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.dto.screening.SeatAvailabilityResponse;
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
import com.ceos23.spring_boot.cgv.service.reservation.ReservationService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ScreeningQueryServiceTest {

    @Autowired
    private ScreeningQueryService screeningQueryService;

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
    @DisplayName("screening query filters by movie and cinema")
    void getScreenings_filtersByMovieAndCinema() {
        ScreeningFixture fixture = createFixture();

        List<Screening> movieScreenings = screeningQueryService.getScreenings(fixture.movie1.getId(), null);
        List<Screening> filteredScreenings = screeningQueryService.getScreenings(
                fixture.movie1.getId(),
                fixture.cinema1.getId()
        );

        assertThat(movieScreenings)
                .extracting(Screening::getId)
                .containsExactly(fixture.screening1.getId(), fixture.screening2.getId());
        assertThat(filteredScreenings)
                .extracting(Screening::getId)
                .containsExactly(fixture.screening1.getId());
    }

    @Test
    @DisplayName("seat availability marks held seats as reserved")
    void getSeatAvailability_marksReservedSeats() {
        ScreeningFixture fixture = createFixture();

        reservationService.createReservation(
                fixture.user.getId(),
                fixture.screening1.getId(),
                List.of(fixture.seatA1.getId())
        );

        SeatAvailabilityResponse response = screeningQueryService.getSeatAvailability(fixture.screening1.getId());

        assertThat(response.screeningId()).isEqualTo(fixture.screening1.getId());
        assertThat(response.seats()).hasSize(2);
        assertThat(response.seats())
                .filteredOn(SeatAvailabilityResponse.SeatStatusResponse::reserved)
                .extracting(SeatAvailabilityResponse.SeatStatusResponse::seatTemplateId)
                .containsExactly(fixture.seatA1.getId());
        assertThat(response.seats())
                .filteredOn(seat -> !seat.reserved())
                .extracting(SeatAvailabilityResponse.SeatStatusResponse::seatTemplateId)
                .containsExactly(fixture.seatA2.getId());
    }

    private ScreeningFixture createFixture() {
        User user = userRepository.save(new User("user1", "user1@example.com", "password", UserRole.USER));
        Cinema cinema1 = cinemaRepository.save(new Cinema("CGV Gangnam", "Seoul"));
        Cinema cinema2 = cinemaRepository.save(new Cinema("CGV Hongdae", "Seoul"));
        SeatLayout seatLayout = seatLayoutRepository.save(new SeatLayout("General", 3, 3));
        Screen screen1 = screenRepository.save(new Screen("1", ScreenType.GENERAL, cinema1, seatLayout));
        Screen screen2 = screenRepository.save(new Screen("2", ScreenType.GENERAL, cinema2, seatLayout));
        Movie movie1 = movieRepository.save(new Movie("Movie 1", 120, "12", "Description"));
        Movie movie2 = movieRepository.save(new Movie("Movie 2", 110, "15", "Description"));
        Screening screening1 = screeningRepository.save(new Screening(
                LocalDateTime.of(2026, 5, 10, 19, 0),
                LocalDateTime.of(2026, 5, 10, 21, 0),
                movie1,
                screen1
        ));
        Screening screening2 = screeningRepository.save(new Screening(
                LocalDateTime.of(2026, 5, 11, 19, 0),
                LocalDateTime.of(2026, 5, 11, 21, 0),
                movie1,
                screen2
        ));
        screeningRepository.save(new Screening(
                LocalDateTime.of(2026, 5, 12, 19, 0),
                LocalDateTime.of(2026, 5, 12, 21, 0),
                movie2,
                screen1
        ));
        SeatTemplate seatA1 = seatTemplateRepository.save(new SeatTemplate("A", 1, seatLayout));
        SeatTemplate seatA2 = seatTemplateRepository.save(new SeatTemplate("A", 2, seatLayout));

        return new ScreeningFixture(user, cinema1, movie1, screening1, screening2, seatA1, seatA2);
    }

    private record ScreeningFixture(
            User user,
            Cinema cinema1,
            Movie movie1,
            Screening screening1,
            Screening screening2,
            SeatTemplate seatA1,
            SeatTemplate seatA2
    ) {
    }
}
