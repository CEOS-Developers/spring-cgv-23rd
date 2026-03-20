package com.ceos.spring_cgv_23rd.domain.review.entity;

import com.ceos.spring_cgv_23rd.domain.movie.entity.Movie;
import com.ceos.spring_cgv_23rd.domain.reservation.entity.Reservation;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(name = "content", nullable = false, length = 200)
    private String content;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "hall_type", nullable = false, length = 20)
    private String hallType;

    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
}
