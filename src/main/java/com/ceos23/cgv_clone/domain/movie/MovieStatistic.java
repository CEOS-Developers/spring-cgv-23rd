package com.ceos23.cgv_clone.domain.movie;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movie_statistics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MovieStatistic {

    @Id
    @Column(name = "movie_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(name = "reservation_rate")
    private double reservationRate;

    @Column(name = "total_viewers")
    private int total;

    @Column(name = "egg_rate")
    private double eggRate;

    @Builder
    public MovieStatistic(Movie movie, double reservationRate, int total, double eggRate) {
        this.movie = movie;
        this.reservationRate = reservationRate;
        this.total = total;
        this.eggRate = eggRate;
    }
}
