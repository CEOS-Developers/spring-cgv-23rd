package com.ceos.spring_cgv_23rd.domain.screening.entity;

import com.ceos.spring_cgv_23rd.domain.movie.entity.Movie;
import com.ceos.spring_cgv_23rd.domain.theater.entity.Hall;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "screening")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "remaining_seats", nullable = false)
    private Integer remainingSeats;

    @Column(name = "price", nullable = false)
    private Integer price;
}
