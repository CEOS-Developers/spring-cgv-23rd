package com.ceos.spring_boot.domain.schedule.entity;

import com.ceos.spring_boot.domain.cinema.entity.Screen;
import com.ceos.spring_boot.domain.movie.entity.Movie;
import com.ceos.spring_boot.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie; // 상영되는 영화

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen; // 상영되는 상영관

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Builder
    public Schedule(Movie movie, Screen screen, LocalDateTime startAt, LocalDateTime endAt) {
        this.movie = movie;
        this.screen = screen;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
