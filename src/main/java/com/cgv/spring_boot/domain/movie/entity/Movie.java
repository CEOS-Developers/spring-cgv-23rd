package com.cgv.spring_boot.domain.movie.entity;

import com.cgv.spring_boot.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    private int runningTime;
    private String rating; // 관람 등급
    private LocalDate releaseDate; // 개봉일

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(columnDefinition = "TEXT")
    private String prologue; // 프롤로그

    private String posterUrl; // 포스터 이미지

    @Builder
    public Movie(String title, int runningTime, String rating, LocalDate releaseDate, Genre genre, String prologue, String posterUrl) {
        this.title = title;
        this.runningTime = runningTime;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.prologue = prologue;
        this.posterUrl = posterUrl;
    }
}
