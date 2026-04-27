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

    // seatCount(총 좌석 수)를 지우고 직사각형의 끝점(행/열)을 명시합니다.
    @Column(name = "max_row", nullable = false, length = 2)
    private String maxRow; // 예: "J" (A~J행까지 존재)

    @Column(name = "max_col", nullable = false)
    private int maxCol;    // 예: 12 (1~12열까지 존재)

    public static Theater create(Cinema cinema, String name, TheaterType type, String maxRow, int maxCol) {
        return Theater.builder()
                .cinema(cinema)
                .name(name)
                .type(type)
                .maxRow(maxRow)
                .maxCol(maxCol)
                .build();
    }
}
