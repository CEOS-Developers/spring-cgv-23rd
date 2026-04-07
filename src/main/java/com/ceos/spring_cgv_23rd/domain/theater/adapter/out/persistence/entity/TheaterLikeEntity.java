package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.persistence.entity;

import com.ceos.spring_cgv_23rd.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "theater_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "theater_id"})
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TheaterLikeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_like_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private TheaterEntity theater;
}
