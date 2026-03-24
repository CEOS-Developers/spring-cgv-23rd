package com.ceos23.spring_cgv_23rd.User.Domain;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import jakarta.persistence.*;

@Entity
public class BookmarkedTheater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
