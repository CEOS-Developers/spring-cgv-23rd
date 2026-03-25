package com.ceos23.cgv_clone.movie.domain;

import com.ceos23.cgv_clone.global.domain.BaseEntity;
import com.ceos23.cgv_clone.theater.domain.Screen;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Builder
    public Schedule(LocalDateTime startAt, LocalDateTime endAt, Screen screen, Movie movie) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.screen = screen;
        this.movie = movie;
    }
}
