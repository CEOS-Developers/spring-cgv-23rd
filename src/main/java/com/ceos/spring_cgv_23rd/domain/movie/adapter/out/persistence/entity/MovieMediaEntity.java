package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.persistence.entity;

import com.ceos.spring_cgv_23rd.domain.movie.domain.MediaType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_media")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;
}
