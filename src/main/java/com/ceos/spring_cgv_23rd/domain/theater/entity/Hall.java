package com.ceos.spring_cgv_23rd.domain.theater.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hall", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"theater_id", "name"})
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hall_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_type_id", nullable = false)
    private HallType hallType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(name = "name", nullable = false, length = 20)
    private String name;
}
