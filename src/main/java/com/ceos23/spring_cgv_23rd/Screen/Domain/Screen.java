package com.ceos23.spring_cgv_23rd.Screen.Domain;

import com.ceos23.spring_cgv_23rd.Movie.Domain.AudienceData;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import jakarta.persistence.*;
import lombok.Builder;

@Builder
@Entity
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String screenName;

    private String cinemaType;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;
}