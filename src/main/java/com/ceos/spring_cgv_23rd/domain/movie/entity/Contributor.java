package com.ceos.spring_cgv_23rd.domain.movie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contributor")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "profile_image_url")
    private String profileImageUrl;
}
