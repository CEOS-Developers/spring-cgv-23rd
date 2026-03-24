package com.ceos23.cgv_clone.theater.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "theaters")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
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
