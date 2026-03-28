package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Movie {

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String director;

    public Movie(String title, String director) {
        this.title = title;
        this.director = director;
    }
}