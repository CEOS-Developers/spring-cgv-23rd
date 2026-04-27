package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity;

import com.ceos.spring_cgv_23rd.domain.movie.domain.AgeRating;
import com.ceos.spring_cgv_23rd.domain.movie.domain.Genre;
import com.ceos.spring_cgv_23rd.domain.movie.domain.MovieStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movie")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "prolog", nullable = false)
    private String prolog;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MovieStatus status;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(name = "age_rating", nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeRating ageRating;

    @Column(name = "released_at", nullable = false)
    private LocalDate releasedAt;

    @Column(name = "poster_url", nullable = false)
    private String posterUrl;

    @OneToOne(mappedBy = "movie", fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private MovieStatisticEntity movieStatistic;

}