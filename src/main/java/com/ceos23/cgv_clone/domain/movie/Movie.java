package com.ceos23.cgv_clone.domain.movie;

import com.ceos23.cgv_clone.domain.user.UserProfile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Movie {

    @Id
    @Column(name = "moive_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movie_name", nullable = false)
    private String name;

    @Column(name = "movie_time", nullable = false)
    private int time;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL)
    private MovieStatistic movieStatistic;

    @Builder
    public Movie(String name, int time) {
        this.name = name;
        this.time = time;
    }
}
