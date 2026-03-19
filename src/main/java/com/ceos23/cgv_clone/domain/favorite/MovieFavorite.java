package com.ceos23.cgv_clone.domain.favorite;

import com.ceos23.cgv_clone.domain.movie.Movie;
import com.ceos23.cgv_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movie_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MovieFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Builder
    public MovieFavorite(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
    }
}
