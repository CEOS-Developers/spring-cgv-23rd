package com.ceos23.spring_boot.domain.user.entity;

import com.ceos23.spring_boot.domain.theater.entity.Theater;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "favorite_theater", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_FAVORITE_THEATER", columnNames = {"user_id", "theater_id"})
})
public class FavoriteTheater extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_theater_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Builder
    public FavoriteTheater(User user, Theater theater) {
        this.user = user;
        this.theater = theater;
    }
}
