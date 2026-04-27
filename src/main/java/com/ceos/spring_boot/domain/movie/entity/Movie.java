package com.ceos.spring_boot.domain.movie.entity;

import com.ceos.spring_boot.domain.cinema.entity.Cinema;
import com.ceos.spring_boot.domain.cinema.entity.CinemaStatus;
import com.ceos.spring_boot.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@Builder
@Table(name = "movies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(nullable = false)
    private String title;      // 영화 제목

    private Integer runningTime; // 상영 시간 (분)

    private String genre;      // 장르

    private LocalDate releaseDate; // 개봉일

    @Enumerated(EnumType.STRING)
    private AgeRating ageRating;

    public static Movie create(String title, Integer runningTime, String genre, LocalDate releaseDate, AgeRating ageRating) {
        return Movie.builder()
                .title(title)
                .runningTime(runningTime)
                .genre(genre)
                .releaseDate(releaseDate)
                .ageRating(ageRating)
                .build();
    }
}