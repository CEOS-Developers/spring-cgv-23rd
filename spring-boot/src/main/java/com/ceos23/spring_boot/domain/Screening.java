package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Screening {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    private Screen screen;

    public Screening(LocalDateTime startTime, Movie movie, Screen screen) {
        this.startTime = startTime;
        this.movie = movie;
        this.screen = screen;
    }
}