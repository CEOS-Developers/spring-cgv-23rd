package com.ceos23.cgv.domain.cinema.entity;

import com.ceos23.cgv.domain.cinema.enums.TheaterType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "theaters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Theater {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TheaterType type;

    @Column(nullable = false)
    private int seatCount;
}
