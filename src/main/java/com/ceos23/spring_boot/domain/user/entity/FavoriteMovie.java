package com.ceos23.spring_boot.domain.user.entity;

import com.ceos23.spring_boot.domain.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "favorite_movie", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_FAVORITE_MOVIE", columnNames = {"user_id", "movie_id"})
})
public class FavoriteMovie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_movie_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
}
