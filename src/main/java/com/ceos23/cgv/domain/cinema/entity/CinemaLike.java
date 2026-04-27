package com.ceos23.cgv.domain.cinema.entity;

import com.ceos23.cgv.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cinema_likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CinemaLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    public static CinemaLike create(User user, Cinema cinema) {
        return CinemaLike.builder()
                .user(user)
                .cinema(cinema)
                .build();
    }
}
