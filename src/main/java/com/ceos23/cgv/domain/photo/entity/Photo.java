package com.ceos23.cgv.domain.photo.entity;

import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.person.entity.Person;
import com.ceos23.cgv.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Photo extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @Column(nullable = false, length = 255)
    private String name; // 사진 파일명 또는 url

    // nullable = true, 영화 사진일 땐 actor가 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Person person;

    // nullable = true, 인물 사진일 땐 movie가 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    public static Photo create(String name, Movie movie, Person person) {
        return Photo.builder()
                .name(name)
                .movie(movie)
                .person(person)
                .build();
    }
}
