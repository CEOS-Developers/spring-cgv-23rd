package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hall_type_id", "row_num", "col_num"})
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_type_id", nullable = false)
    private HallTypeEntity hallType;

    @Column(name = "row_num", nullable = false)
    private Integer rowNum;

    @Column(name = "col_num", nullable = false)
    private Integer colNum;

    @Builder.Default
    @Column(name = "is_usable", nullable = false)
    private Boolean isUsable = true;
}
