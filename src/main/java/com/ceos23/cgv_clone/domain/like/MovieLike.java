package com.ceos23.cgv_clone.domain.like;

import com.ceos23.cgv_clone.domain.movie.Movie;
import com.ceos23.cgv_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movie_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Builder
    public MovieLike(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
    }
}
