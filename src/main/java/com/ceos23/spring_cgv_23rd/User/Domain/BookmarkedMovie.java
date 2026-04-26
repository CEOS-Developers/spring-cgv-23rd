package com.ceos23.spring_cgv_23rd.User.Domain;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkedMovie {
    private BookmarkedMovie(User user, Movie movie){
        this.user = user;
        this.movie = movie;
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    public static BookmarkedMovie create(User user, Movie movie){
        return new BookmarkedMovie(user, movie);
    }
}
