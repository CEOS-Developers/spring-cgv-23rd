package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.Movie;
import com.ceos23.spring_boot.domain.Screen;
import com.ceos23.spring_boot.domain.ScreenType;
import com.ceos23.spring_boot.domain.Screening;
import com.ceos23.spring_boot.domain.Seat;
import com.ceos23.spring_boot.domain.Theater;
import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.repository.MovieRepository;
import com.ceos23.spring_boot.repository.ReservationRepository;
import com.ceos23.spring_boot.repository.ScreenRepository;
import com.ceos23.spring_boot.repository.ScreeningRepository;
import com.ceos23.spring_boot.repository.SeatRepository;
import com.ceos23.spring_boot.repository.TheaterRepository;
import com.ceos23.spring_boot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("같은 좌석을 동시에 예매하면 1번만 성공한다")
    void reserveSameSeatConcurrencyTest() throws InterruptedException {
        User user1 = userRepository.save(User.of("user1", "1234"));
        User user2 = userRepository.save(User.of("user2", "1234"));

        Theater theater = theaterRepository.save(new Theater("강남 CGV", "서울"));
        Movie movie = movieRepository.save(new Movie("아바타", "제임스 카메론"));
        Screen screen = screenRepository.save(new Screen(ScreenType.values()[0], theater));
        Screening screening = screeningRepository.save(
                new Screening(LocalDateTime.now().plusDays(1), movie, screen)
        );
        Seat seat = seatRepository.save(new Seat(1, 1, screen));

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<Long> userIds = List.of(user1.getId(), user2.getId());

        for (Long userId : userIds) {
            executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    reservationService.reserve(userId, screening.getId(), seat.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        long reservationCount = reservationRepository
                .count();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(reservationCount).isEqualTo(1);
    }
}