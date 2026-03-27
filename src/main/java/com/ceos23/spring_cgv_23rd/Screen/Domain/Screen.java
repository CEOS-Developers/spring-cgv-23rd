package com.ceos23.spring_cgv_23rd.Screen.Domain;

import com.ceos23.spring_cgv_23rd.Movie.Domain.AudienceData;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Screen {
    private Screen(String scName, CinemaType cinemaType, Theater theater, int seatAmount){
        this.screenName = scName;
        this.cinemaType = cinemaType;
        this.theater = theater;
        this.seatAmount = seatAmount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String screenName;

    private CinemaType cinemaType;

    private int seatAmount;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    public static Screen create(Theater theater, String scName, CinemaType cType, int seatAmount){
        return new Screen(scName, cType, theater, seatAmount);
    }


}