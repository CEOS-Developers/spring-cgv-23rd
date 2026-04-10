package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY로 변경
    private Long id;

    @Enumerated(EnumType.STRING)
    private ScreenType type;  // String → Enum으로 변경

    @ManyToOne
    private Theater theater;

    public Screen(ScreenType type, Theater theater) {
        this.type = type;
        this.theater = theater;
    }
}