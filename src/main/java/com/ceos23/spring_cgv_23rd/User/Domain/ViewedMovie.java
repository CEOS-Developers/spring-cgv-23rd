package com.ceos23.spring_cgv_23rd.User.Domain;

import com.ceos23.spring_cgv_23rd.Movie.Domain.Movie;
import jakarta.persistence.*;

@Entity
public class ViewedMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private boolean canceled;

    private boolean expired;
}