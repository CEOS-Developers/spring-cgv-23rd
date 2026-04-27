package com.ceos23.cgv.domain.movie.entity;

import com.ceos23.cgv.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MovieLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    public static MovieLike create(User user, Movie movie) {
        return MovieLike.builder()
                .user(user)
                .movie(movie)
                .build();
    }
}
