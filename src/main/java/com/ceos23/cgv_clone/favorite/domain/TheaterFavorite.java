package com.ceos23.cgv_clone.favorite.domain;

import com.ceos23.cgv_clone.theater.domain.Theater;
import com.ceos23.cgv_clone.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "theater_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TheaterFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Builder
    public TheaterFavorite(User user, Theater theater) {
        this.user = user;
        this.theater = theater;
    }
}
