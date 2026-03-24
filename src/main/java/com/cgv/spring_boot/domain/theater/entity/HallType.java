package com.cgv.spring_boot.domain.theater.entity;

import com.cgv.spring_boot.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HallType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String typeName;

    private int colCount;
    private int rowCount;

    @Builder
    public HallType(String typeName, int colCount, int rowCount) {
        this.typeName = typeName;
        this.colCount = colCount;
        this.rowCount = rowCount;
    }
}
