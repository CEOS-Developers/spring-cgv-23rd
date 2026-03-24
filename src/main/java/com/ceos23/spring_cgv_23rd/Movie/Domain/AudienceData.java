package com.ceos23.spring_cgv_23rd.Movie.Domain;

import com.ceos23.spring_cgv_23rd.Actor.Domain.ActorInfo;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudienceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private double womenRate;

    private double menRate;

    private double teenRate;

    private double twRate;

    private double thRate;

    private double frRate;

    private double ftRate;

    public void addMovie(Movie movie){
        this.movie = movie;
        movie.setAudienceData(this);
    }

}
