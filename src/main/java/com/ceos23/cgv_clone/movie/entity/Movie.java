package com.ceos23.cgv_clone.movie.entity;

import com.ceos23.cgv_clone.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Movie extends BaseEntity {

    @Id
    @Column(name = "movie_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int runningTime;

    @Column(nullable = false)
    private int ageRestriction;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL)
    private MovieStatistic movieStatistic;

    @Builder
    public Movie(String name, int runningTime, int ageRestriction) {
        this.name = name;
        this.runningTime = runningTime;
        this.ageRestriction = ageRestriction;
    }
}
