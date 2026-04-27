package com.ceos23.cgv_clone.store.entity;

import com.ceos23.cgv_clone.global.entity.BaseEntity;
import com.ceos23.cgv_clone.theater.entity.Theater;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @JoinColumn(name = "theater_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Theater theater;

    @Builder
    public Store(Theater theater) {
        this.theater = theater;
    }
}
