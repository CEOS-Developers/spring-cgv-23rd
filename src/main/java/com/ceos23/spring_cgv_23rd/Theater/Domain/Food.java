package com.ceos23.spring_cgv_23rd.Theater.Domain;

import com.ceos23.spring_cgv_23rd.Media.Domain.Media;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String menuName;

    private int price;

    @OneToMany
    @JoinColumn(name = "food_photos")
    private List<Media> foodPhotos = new ArrayList<>();
}
