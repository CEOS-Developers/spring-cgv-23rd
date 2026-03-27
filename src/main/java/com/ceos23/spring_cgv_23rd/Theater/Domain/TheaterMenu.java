package com.ceos23.spring_cgv_23rd.Theater.Domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TheaterMenu {
    private TheaterMenu(Food food, Theater theater, boolean soldOut, int amount){
        this.food = food;
        this.theater = theater;
        this.soldOut = soldOut;
        this.amount = amount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean soldOut;

    private int amount;

    @ManyToOne
    @JoinColumn(name = "food")
    private Food food;

    @Setter
    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;

    public int getPrice(){
        return food.getPrice();
    }

    public void addTheaterInEntity(Theater ttr){
        this.theater = ttr;
        ttr.getTheaterMenus().add(this);
    }

    public void chargeAmount(int amount){
        this.amount += amount;
    }

    public void dischargeAmount(int amount){
        this.amount -= amount;
    }

    public static TheaterMenu create(Food food, Theater theater, int amount){
        return new TheaterMenu(food, theater, false, amount);
    }
}
