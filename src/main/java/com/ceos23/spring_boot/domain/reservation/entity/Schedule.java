package com.ceos23.spring_boot.domain.reservation.entity;

import com.ceos23.spring_boot.domain.movie.entity.Movie;
import com.ceos23.spring_boot.domain.theater.entity.Screen;
import com.ceos23.spring_boot.global.common.BaseSoftDeleteEntity;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseSoftDeleteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "base_price")
    private Integer basePrice;

    @Builder
    public Schedule(Screen screen, Movie movie, LocalDateTime startTime, LocalDateTime endTime, Integer basePrice) {
        this.screen = screen;
        this.movie = movie;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
    }

    public void validateReservableTime(LocalDateTime now) {
        if (now.isAfter(this.startTime)) {
            throw new BusinessException(ErrorCode.SCHEDULE_ALREADY_STARTED);
        }
    }
}
