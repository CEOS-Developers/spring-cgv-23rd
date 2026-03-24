package com.cgv.spring_boot.domain.theater.entity;

import com.cgv.spring_boot.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Theater extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location; // 서울, 경기 등
    private String address;

    @Builder
    public Theater(String name, String location, String address) {
        this.name = name;
        this.location = location;
        this.address = address;
    }
}
