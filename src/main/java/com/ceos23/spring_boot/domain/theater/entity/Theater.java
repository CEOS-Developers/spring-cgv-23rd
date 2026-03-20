package com.ceos23.spring_boot.domain.theater.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theater {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String location;

    @Builder
    public Theater(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public void update(String name, String location) {
        this.name = name;
        this.location = location;
    }
}

