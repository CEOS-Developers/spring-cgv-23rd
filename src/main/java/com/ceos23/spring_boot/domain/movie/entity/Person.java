package com.ceos23.spring_boot.domain.movie.entity;

import com.ceos23.spring_boot.global.common.BaseSoftDeleteEntity;
import com.ceos23.spring_boot.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Person extends BaseSoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private LocalDate birthday;

    @Column(length = 50)
    private String birthplace;

    @Column(name = "profile_image", length = 255)
    private String profileImage;
}
