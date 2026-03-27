package com.ceos23.spring_cgv_23rd.Movie.Domain;

import com.ceos23.spring_cgv_23rd.Actor.Domain.ActorInfo;
import com.ceos23.spring_cgv_23rd.Media.Domain.Media;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {
    private Movie(String name, LocalDate openDate, String prolog, AccessibleAge aca, MovieType movieType, int price, long runningTime){
        this.movieName = name;
        this.openDate = openDate;
        this.prolog = prolog;
        this.accessibleAge = aca;
        this.movieType = movieType;
        this.price = price;
        this.runningTime = runningTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @OneToOne(mappedBy = "movie")
    private AudienceData audienceData;

    @OneToMany(mappedBy = "movie")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany()
    @JoinColumn(name = "movie_photos")
    private List<Media> photo = new ArrayList<>();

    @OneToMany()
    @JoinColumn(name = "video_photos")
    private List<Media> video = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private List<ActorInfo> actors = new ArrayList<>();

    private String movieName;

    private LocalDate openDate;

    private double reservRate;

    private double eggRate;

    private String prolog;

    @Enumerated(EnumType.STRING)
    private AccessibleAge accessibleAge;

    private MovieType movieType;

    private int price;

    private long runningTime;

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

    public static Movie create(String name, LocalDate date, String prolog, AccessibleAge aca, MovieType type,
                               int price, long runningTime) {
        return new Movie(name, date, prolog, aca, type, price, runningTime);
    }
}