package com.ceos23.cgv_clone.domain.theater;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Theaters")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long id;

    @Column(name = "theater_name", nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(nullable = false)
    private String address;

    @Builder
    public Theater(String name, String region, String address) {
        this.name = name;
        this.region = region;
        this.address = address;
    }
}
