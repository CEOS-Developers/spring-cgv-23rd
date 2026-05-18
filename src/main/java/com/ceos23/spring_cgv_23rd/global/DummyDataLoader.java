package com.ceos23.spring_cgv_23rd.global;

import com.ceos23.spring_cgv_23rd.Movie.Domain.AccessibleAge;
import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.Movie.Domain.MovieType;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screen;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreenRepository;
import com.ceos23.spring_cgv_23rd.Screen.Repository.ScreeningRepository;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Region;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Repository.TheaterRepository;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Profile("k6")
@Transactional
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;

    @Override
    public void run(String... args) throws Exception {
        String encodedPassword = passwordEncoder.encode("test123!!");

        Faker faker = new Faker(new Locale("ko"));
        List<Theater> theaters = new ArrayList<>();
        List<Screen> screens = new ArrayList<>();
        List<Screening> screenings = new ArrayList<>();
        List<Movie> movies = new ArrayList<>();
        List<User> users = new ArrayList<>();

        try {// create(String loginId, String username, String password, boolean men, int age)
            for (int i = 0; i < 2; i++) {
                User user = User.create(
                        "user" + i, "테스트" + i, encodedPassword, true, 21
                );
                users.add(user);
            }

            for (int i = 0; i < 2; i++) {
                Theater theater = Theater.create(
                        faker.name().name(), Region.SEOUL.getRegionName()
                );
                theaters.add(theater);
            }


            for (int i = 0; i < 2; i++) {
                for (Theater th : theaters) {
                    Screen screen = Screen.create(
                            th, String.format("%d관-%s", i, th.getName()), CinemaType.NORMAL, 172
                    );
                    screens.add(screen);
                }
            }


            for (int i = 0; i < 10000; i++) {
                Movie movie = Movie.create(
                        faker.book().title(), faker.date().birthdayLocalDate(), faker.lorem().characters(30),
                        AccessibleAge.ALL, MovieType.FAMILY, 15000, faker.number().numberBetween(90, 200)
                );
                movies.add(movie);
            }

            for (int i = 0; i < 2; i++) {
                for (Screen s : screens) {
                    for (Movie m : movies) {
                        Screening screening = Screening.create(
                                s, m, LocalDateTime.of(LocalDate.of(2026, 5, 28), LocalTime.of(7, 0).plusHours(4))
                        );

                        screenings.add(screening);
                    }
                }
            }

            userRepository.saveAll(users);

            theaterRepository.saveAll(theaters);

            movieRepository.saveAll(movies);

            screenRepository.saveAll(screens);

            screeningRepository.saveAll(screenings);
        } catch (Exception e) {
            System.out.println("더미데이터 추가 에러 >>> ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
