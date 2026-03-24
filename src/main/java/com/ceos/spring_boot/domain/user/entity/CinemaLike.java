package com.ceos.spring_boot.domain.user.entity;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "movie_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CinemaLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

}