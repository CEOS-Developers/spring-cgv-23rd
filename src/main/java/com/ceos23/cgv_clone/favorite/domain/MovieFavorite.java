package com.ceos23.cgv_clone.favorite.domain;

import com.ceos23.cgv_clone.movie.domain.Movie;
import com.ceos23.cgv_clone.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
// Unique 제약 걸어서 중복 찜 방지
@Table(
        name = "movie_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
)
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
