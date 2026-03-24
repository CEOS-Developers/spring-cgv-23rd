package com.ceos.spring_cgv_23rd.domain.movie.entity;

import com.ceos.spring_cgv_23rd.domain.movie.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie_media")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name ="media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;
}
