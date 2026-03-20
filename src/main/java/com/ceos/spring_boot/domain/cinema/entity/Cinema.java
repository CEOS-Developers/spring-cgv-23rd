package com.ceos.spring_boot.domain.cinema.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@AllArgsConstructor
@Builder
@Table(name = "cinemas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_id")
    private Long id;

    private String name; // 영화관 지점명

    private String region; // 지역

    private String address; // 주소

    @Enumerated(EnumType.STRING)
    private CinemaStatus status; // 지점 상태(운영 중, 운영 마감, 폐점 등)
}
