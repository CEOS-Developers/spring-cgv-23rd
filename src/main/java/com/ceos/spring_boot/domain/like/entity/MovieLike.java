package com.ceos.spring_boot.domain.like.entity;

import com.ceos.spring_boot.domain.movie.entity.Movie;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(
        name = "movie_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_movie",
                        columnNames = {"user_id", "movie_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieLike extends BaseEntity {

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

    public static MovieLike create(User user, Movie movie) {
        return MovieLike.builder()
                .user(user)
                .movie(movie)
                .build();
    }
}
