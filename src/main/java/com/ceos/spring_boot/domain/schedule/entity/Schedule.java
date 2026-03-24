package com.ceos.spring_boot.domain.schedule.entity;

import com.ceos.spring_boot.domain.cinema.entity.Screen;
import com.ceos.spring_boot.domain.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

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
    private LocalDate startDate; // 상영 날짜 (예: 2024-03-20)

    @Column(nullable = false)
    private LocalTime startTime; // 상영 시작 시간 (예: 14:30)

    @Column(nullable = false)
    private LocalTime endTime;   // 상영 종료 시간 (예: 16:30)
}
