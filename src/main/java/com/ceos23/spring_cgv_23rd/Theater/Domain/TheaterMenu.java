package com.ceos23.spring_cgv_23rd.Theater.Domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TheaterMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean soldOut;

    @ManyToOne
    @JoinColumn(name = "food")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    public void addTheaterInEntity(Theater ttr){
        this.theater = ttr;
        ttr.getTheaterMenus().add(this);
    }
}
