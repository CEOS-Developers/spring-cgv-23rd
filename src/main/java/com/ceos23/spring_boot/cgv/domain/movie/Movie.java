package com.ceos23.spring_boot.cgv.domain.movie;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private Integer runningTime;

    @Column(nullable = false, length = 20)
    private String rating;

    @Column(nullable = false, length = 500)
    private String description;

    public Movie(String title, Integer runningTime, String rating, String description) {
        this.title = title;
        this.runningTime = runningTime;
        this.rating = rating;
        this.description = description;
    }
}