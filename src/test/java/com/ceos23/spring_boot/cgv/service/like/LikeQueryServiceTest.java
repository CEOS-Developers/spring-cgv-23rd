package com.ceos23.spring_boot.cgv.service.like;

import static org.assertj.core.api.Assertions.assertThat;

import com.ceos23.spring_boot.cgv.domain.cinema.Cinema;
import com.ceos23.spring_boot.cgv.domain.like.CinemaLike;
import com.ceos23.spring_boot.cgv.domain.like.MovieLike;
import com.ceos23.spring_boot.cgv.domain.movie.Movie;
import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.repository.cinema.CinemaRepository;
import com.ceos23.spring_boot.cgv.repository.like.CinemaLikeRepository;
import com.ceos23.spring_boot.cgv.repository.like.MovieLikeRepository;
import com.ceos23.spring_boot.cgv.repository.movie.MovieRepository;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import com.ceos23.spring_boot.cgv.service.cinema.CinemaLikeService;
import com.ceos23.spring_boot.cgv.service.movie.MovieLikeService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LikeQueryServiceTest {

    @Autowired
    private CinemaLikeService cinemaLikeService;

    @Autowired
    private MovieLikeService movieLikeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaLikeRepository cinemaLikeRepository;

    @Autowired
    private MovieLikeRepository movieLikeRepository;

    @AfterEach
    void tearDown() {
        cinemaLikeRepository.deleteAllInBatch();
        movieLikeRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
        cinemaRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("liked cinema and movie lists return the user's likes")
    void getLikes_returnsLikedItems() {
        User user = userRepository.save(new User("user1", "user1@example.com", "password", UserRole.USER));
        Cinema cinema = cinemaRepository.save(new Cinema("CGV Gangnam", "Seoul"));
        Movie movie = movieRepository.save(new Movie("Movie", 120, "12", "Description"));

        cinemaLikeService.likeCinema(user.getId(), cinema.getId());
        movieLikeService.likeMovie(user.getId(), movie.getId());

        List<CinemaLike> likedCinemas = cinemaLikeService.getLikedCinemas(user.getId());
        List<MovieLike> likedMovies = movieLikeService.getLikedMovies(user.getId());

        assertThat(likedCinemas)
                .extracting(like -> like.getCinema().getName())
                .containsExactly("CGV Gangnam");
        assertThat(likedMovies)
                .extracting(like -> like.getMovie().getTitle())
                .containsExactly("Movie");
    }
}
