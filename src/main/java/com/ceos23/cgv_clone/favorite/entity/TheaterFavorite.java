package com.ceos23.cgv_clone.favorite.entity;

import com.ceos23.cgv_clone.global.entity.BaseEntity;
import com.ceos23.cgv_clone.theater.entity.Theater;
import com.ceos23.cgv_clone.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
// Unique 제약 걸어서 중복 찜 방지
@Table(
        name = "theater_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "theater_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TheaterFavorite extends BaseEntity {

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

    public static TheaterFavorite create(User user, Theater theater) {
        return TheaterFavorite.builder()
                .user(user)
                .theater(theater)
                .build();
    }
}
