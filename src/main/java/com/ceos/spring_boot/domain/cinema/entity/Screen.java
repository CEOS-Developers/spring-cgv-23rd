package com.ceos.spring_boot.domain.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "screens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType; // GENERAL, SPECIAL

    private String name; // 상영관 이름
}
