package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;

@Entity
@Getter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // null 못 들어가게 (데이터 정합성을 위해)
    private String title;

    private String director;

    public Movie(String title, String director) {
        this.title = title;
        this.director = director;
    }
}