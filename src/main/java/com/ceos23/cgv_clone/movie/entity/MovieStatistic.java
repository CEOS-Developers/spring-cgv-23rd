package com.ceos23.cgv_clone.movie.entity;

import com.ceos23.cgv_clone.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movie_statistics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MovieStatistic extends BaseEntity {

    @Id
    @Column(name = "movie_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(nullable = false)
    private double reservationRate;

    @Column(nullable = false)
    private int totalViewers;

    @Column(nullable = false)
    private double eggRate;

    @Builder
    public MovieStatistic(Movie movie, double reservationRate, int totalViewers, double eggRate) {
        this.movie = movie;
        this.reservationRate = reservationRate;
        this.totalViewers = totalViewers;
        this.eggRate = eggRate;
    }
}
