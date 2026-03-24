package com.ceos.spring_cgv_23rd.domain.movie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_statistic")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieStatistic {

    @Id
    @Column(name = "movie_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Builder.Default
    @Column(name = "reservation_rate", nullable = false)
    private Double reservationRate = 0.0;

    @Builder.Default
    @Column(name = "reservation_rank", nullable = false)
    private Integer reservationRank = 0;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Builder.Default
    @Column(name = "egg_count", nullable = false)
    private Double eggCount = 0.0;

    @Builder.Default
    @Column(name = "male_reservation_rate", nullable = false)
    private Double maleReservationRate = 0.0;

    @Builder.Default
    @Column(name = "female_reservation_rate", nullable = false)
    private Double femaleReservationRate = 0.0;

    @Builder.Default
    @Column(name = "age_10s_rate", nullable = false)
    private Double age10sRate = 0.0;

    @Builder.Default
    @Column(name = "age_20s_rate", nullable = false)
    private Double age20sRate = 0.0;

    @Builder.Default
    @Column(name = "age_30s_rate", nullable = false)
    private Double age30sRate = 0.0;

    @Builder.Default
    @Column(name = "age_40s_rate", nullable = false)
    private Double age40sRate = 0.0;

    @Builder.Default
    @Column(name = "age_50s_rate", nullable = false)
    private Double age50sRate = 0.0;

}
