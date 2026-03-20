package com.ceos.spring_boot.domain.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@Table(name = "seats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    private String seatRow; // 행 (A, B, C...)
    private Integer seatCol; // 열 (1, 2, 3...)
}