package com.ceos23.spring_boot.domain.movie.entity;

import com.ceos23.spring_boot.global.common.BaseSoftDeleteEntity;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Movie", columnNames = {"title", "release_date"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseSoftDeleteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private Integer runtime;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "age_rating", nullable = false, length = 50)
    private String ageRating;

    @Column(name = "average_rating", precision = 3, scale = 1)
    private BigDecimal averageRating;

    @Column(name = "poster_url", length = 255)
    private String posterUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder
    public Movie(String title, Integer runtime, LocalDate releaseDate, String ageRating, String posterUrl, String description) {
        this.title = title;
        this.runtime = runtime;
        this.releaseDate = releaseDate;
        this.ageRating = ageRating;
        this.posterUrl = posterUrl;
        this.description = description;
    }

    public void update(String title, Integer runtime, LocalDate releaseDate, String ageRating, String posterUrl, String description) {
        this.title = title;
        this.runtime = runtime;
        this.releaseDate = releaseDate;
        this.ageRating = ageRating;
        this.posterUrl = posterUrl;
        this.description = description;
    }

    public boolean uniqueKeyChanged(String title, LocalDate releaseDate) {
        return !this.title.equals(title) ||
                !this.releaseDate.isEqual(releaseDate);
    }
}
