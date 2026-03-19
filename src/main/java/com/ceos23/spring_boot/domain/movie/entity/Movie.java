package com.ceos23.spring_boot.domain.movie.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private Integer runtime;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "age_rating", nullable = false, length = 50)
    private String ageRating;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 1)
    private BigDecimal averageRating;

    @Column(name = "poster_url", length = 255)
    private String posterUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
}
