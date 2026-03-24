package com.ceos.spring_cgv_23rd.domain.movie.entity;

import com.ceos.spring_cgv_23rd.domain.movie.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_credit", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"movie_id", "contributor_id", "role_type"})
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_credit_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id", nullable = false)
    private Contributor contributor;

    @Column(name = "role_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
}
