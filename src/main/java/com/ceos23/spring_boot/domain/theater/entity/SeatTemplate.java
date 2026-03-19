package com.ceos23.spring_boot.domain.theater.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UQ_SEAT_TEMPLATE_COORD", columnNames = {"screen_type_id", "seat_row", "seat_col"})
})
public class SeatTemplate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_template_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_type_id", nullable = false)
    private ScreenType screenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id", nullable = false)
    private SeatGrade seatGrade;

    @Column(name = "seat_row", nullable = false, length = 50)
    private String seatRow;

    @Column(name = "seat_col", nullable = false)
    private Integer seatCol;
}
