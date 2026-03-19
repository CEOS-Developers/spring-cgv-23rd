package com.ceos23.spring_boot.domain.user.entity;

import com.ceos23.spring_boot.domain.theater.entity.Theater;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "favorite_theater", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_FAVORITE_THEATER", columnNames = {"user_id", "theater_id"})
})
public class FavoriteTheater {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_theater_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;
}
