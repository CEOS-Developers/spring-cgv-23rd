package com.ceos23.spring_boot.domain.theater.entity;

import com.ceos23.spring_boot.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UQ_SEAT_COORD", columnNames = {"screen_id", "seat_row", "seat_col"})
})
public class Seat extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id")
    private SeatGrade seatGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "row_name", nullable = false, length = 50)
    private String rowName;

    @Column(name = "col_number", nullable = false)
    private Integer colNumber;

    @Builder
    public Seat(SeatGrade seatGrade, Screen screen, String rowName, Integer colNumber) {
        this.seatGrade = seatGrade;
        this.screen = screen;
        this.rowName = rowName;
        this.colNumber = colNumber;
    }
}