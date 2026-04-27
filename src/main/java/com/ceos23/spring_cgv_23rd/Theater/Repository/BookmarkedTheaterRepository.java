package com.ceos23.spring_cgv_23rd.Theater.Repository;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.User.Domain.BookmarkedTheater;
import com.ceos23.spring_cgv_23rd.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface BookmarkedTheaterRepository extends JpaRepository<BookmarkedTheater, Long> {
    boolean existsBytheaterAndUser(Theater theater, User user);

    void deleteBookmarkedTheaterById(long id);

    Optional<BookmarkedTheater> findByTheaterAndUser(Theater theater, User user);

    List<BookmarkedTheater> findByUser(User user);
}
