package com.ceos23.cgv.domain.person.entity;

import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.person.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "work_participations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorkParticipation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_participation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType role;

    public static WorkParticipation create(Movie movie, Person person, RoleType role) {
        return WorkParticipation.builder()
                .movie(movie)
                .person(person)
                .role(role)
                .build();
    }
}
