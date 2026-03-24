package com.ceos.spring_boot.domain.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@Builder
@Table(name = "movies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(nullable = false)
    private String title;      // 영화 제목

    private Integer runningTime; // 상영 시간 (분)

    private String genre;      // 장르

    private LocalDate releaseDate; // 개봉일

    @Enumerated(EnumType.STRING)
    private AgeRating ageRating;
}