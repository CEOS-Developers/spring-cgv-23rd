package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity;

import com.ceos.spring_cgv_23rd.domain.movie.domain.RoleType;
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
public class MovieCreditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_credit_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id", nullable = false)
    private ContributorEntity contributor;

    @Column(name = "role_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
}
