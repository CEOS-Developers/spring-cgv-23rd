package com.ceos23.cgv.domain.movie.entity;

import com.ceos23.cgv.domain.movie.enums.Genre;
import com.ceos23.cgv.domain.movie.enums.MovieRating;
import com.ceos23.cgv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Movie extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private int runningTime; // 상영 시간(분)

    @Column(nullable = false)
    private Double salesRate; //예매율

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovieRating movieRating; // 관람 등급

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Genre genre;

    @Column(columnDefinition = "TEXT")
    private String prologue;
}
