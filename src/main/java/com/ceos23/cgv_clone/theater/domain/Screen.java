package com.ceos23.cgv_clone.theater.domain;

import com.ceos23.cgv_clone.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "screens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Screen extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_type_id", nullable = false)
    private ScreenType screenType;

    @Builder
    public Screen(String name, Theater theater, ScreenType screenType) {
        this.name = name;
        this.theater = theater;
        this.screenType = screenType;
    }
}
