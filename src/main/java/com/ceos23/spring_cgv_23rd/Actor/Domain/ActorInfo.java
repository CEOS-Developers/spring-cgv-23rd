package com.ceos23.spring_cgv_23rd.Actor.Domain;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActorInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Setter
    @ManyToOne
    @JoinColumn(name = "actor_id")
    private Actor actor;

    public void addActor(Actor ac){
        actor = ac;
        ac.getActorInfos().add(this);
    }

    public void addMovie(Movie mv){
        movie = mv;
        mv.getActors().add(this);
    }
}
