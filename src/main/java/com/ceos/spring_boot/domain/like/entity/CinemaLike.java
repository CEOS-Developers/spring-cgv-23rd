package com.ceos.spring_boot.domain.like.entity;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(
        name = "cinema_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_cinema",
                        columnNames = {"user_id", "cinema_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CinemaLike extends BaseEntity {

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

    @Builder
    public CinemaLike(User user, Cinema cinema) {
        this.user = user;
        this.cinema = cinema;
    }
}