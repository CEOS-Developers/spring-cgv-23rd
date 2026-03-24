package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Screen {

    @Id @GeneratedValue
    private Long id;

    private String type; // 일반관 / 특별관

    @ManyToOne
    private Theater theater;

    public Screen(String type, Theater theater) {
        this.type = type;
        this.theater = theater;
    }
}