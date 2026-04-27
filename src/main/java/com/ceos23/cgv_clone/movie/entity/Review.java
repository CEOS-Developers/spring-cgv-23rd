package com.ceos23.cgv_clone.movie.entity;

import com.ceos23.cgv_clone.global.entity.BaseEntity;
import com.ceos23.cgv_clone.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Builder
    public Review(String content, User user, Movie movie) {
        this.content = content;
        this.user = user;
        this.movie = movie;
    }
}
