package com.ceos23.spring_cgv_23rd.Movie.Repository;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import com.ceos23.spring_cgv_23rd.User.Domain.BookmarkedMovie;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkedMovieRepository extends JpaRepository<BookmarkedMovie, Long> {
    Optional<BookmarkedMovie> findByMovieAndUser(Movie movie, User user);

    void deleteBookmarkedMovieByMovieId(long movieId);

    void deleteBookmarkedMovieById(long id);

    List<BookmarkedMovie> findByUser(User user);
}
