package com.ceos23.spring_cgv_23rd.Movie.Domain;

import com.ceos23.spring_cgv_23rd.Actor.Domain.ActorInfo;
import com.ceos23.spring_cgv_23rd.Media.Domain.Media;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "movie")
    private AudienceData audienceData;

    @Builder.Default
    @OneToMany(mappedBy = "movie")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany()
    @Builder.Default
    @JoinColumn(name = "movie_photos")
    private List<Media> photo = new ArrayList<>();

    @OneToMany()
    @Builder.Default
    @JoinColumn(name = "video_photos")
    private List<Media> video = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "movie")
    private List<ActorInfo> actors = new ArrayList<>();

    private String movieName;

    private LocalDateTime openDate;

    private double reservRate;

    private double eggRate;

    private String prolog;

    @Enumerated(EnumType.STRING)
    private AccessibleAge accessibleAge;

    private MovieType movieType;

    public void addAudienceDataInMovie(AudienceData aud){
        this.audienceData = aud;
        aud.setMovie(this);
    }

    public void addComment(Comment cmm){
        comments.add(cmm);
        cmm.setMovie(this);
    }

    public void addActorInfo(ActorInfo ai){
        actors.add(ai);
        ai.setMovie(this);
    }

}