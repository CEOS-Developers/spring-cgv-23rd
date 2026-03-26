package com.ceos23.spring_boot.domain.movie.entity;

import com.ceos23.spring_boot.domain.user.entity.User;
import com.ceos23.spring_boot.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_REVIEW_USER_MOVIE", columnNames = {"user_id", "movie_id"})
})
public class Review extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal rating;
}