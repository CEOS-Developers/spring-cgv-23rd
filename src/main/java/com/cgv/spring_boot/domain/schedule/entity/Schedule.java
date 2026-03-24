package com.cgv.spring_boot.domain.schedule.entity;

import com.cgv.spring_boot.domain.movie.entity.Movie;
import com.cgv.spring_boot.domain.theater.entity.Hall;
import com.cgv.spring_boot.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Builder
    public Schedule(LocalDateTime startTime, Movie movie, Hall hall) {
        this.startTime = startTime;
        this.movie = movie;
        this.hall = hall;
    }
}
