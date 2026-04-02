package com.ceos23.spring_boot.domain.theater.entity;

import com.ceos23.spring_boot.global.common.BaseSoftDeleteEntity;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
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
public class SeatTemplate extends BaseSoftDeleteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_template_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_type_id", nullable = false)
    private ScreenType screenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id")
    private SeatGrade seatGrade;

    @Column(name = "row_name", nullable = false, length = 50)
    private String rowName;

    @Column(name = "col_number", nullable = false)
    private Integer colNumber;
}
