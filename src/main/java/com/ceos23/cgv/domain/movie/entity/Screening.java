package com.ceos23.cgv.domain.movie.entity;

import com.ceos23.cgv.domain.cinema.entity.Theater;
import com.ceos23.cgv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "screenings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Screening extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Boolean isMorning; //조조영화 여부 (영화마다 기준이 다르다?)

    public static Screening create(Movie movie, Theater theater, LocalDateTime startTime,
                                   LocalDateTime endTime, Boolean isMorning) {
        return Screening.builder()
                .movie(movie)
                .theater(theater)
                .startTime(startTime)
                .endTime(endTime)
                .isMorning(isMorning)
                .build();
    }
}
