package com.ceos23.cgv_clone.theater.entity;

import com.ceos23.cgv_clone.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "theaters")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Theater extends BaseEntity {

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
