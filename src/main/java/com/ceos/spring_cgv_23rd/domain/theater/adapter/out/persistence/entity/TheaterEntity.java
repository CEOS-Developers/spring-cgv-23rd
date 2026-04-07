package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "theater")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TheaterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_opened", nullable = false)
    private boolean isOpened = true;
}
