package com.ceos23.spring_cgv_23rd.Theater.Domain;

import com.ceos23.spring_cgv_23rd.Food.Domain.Food;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TheaterMenu {
    private TheaterMenu(Food food, Theater theater, boolean soldOut, int amount){
        this.food = food;
        this.theater = theater;
        this.soldOut = soldOut;
        this.sold = amount;
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    private int sold;

    @Getter
    private boolean soldOut;

    @Getter
    private boolean ablePickUpLater;

    @Getter
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

    public void charge(int amount){
        this.sold += amount;
        soldOut = true;
    }

    public void buy(int amount){
        if (soldOut || sold < amount){
            throw new CustomException(ErrorCode.INVENTORY_SHORTAGE);
        }
        this.sold -= amount;
        if (sold == 0){
            soldOut = true;
        }
    }

    public static TheaterMenu create(Food food, Theater theater, int amount){
        if (amount == 0){
            TheaterMenu tm = new TheaterMenu(food, theater, true, amount);
            tm.addTheaterInEntity(theater);

            return tm;
        } else if (amount < 0) {
            throw new CustomException(ErrorCode.INVALID_QUANTITY);
        } else {
            TheaterMenu tm = new TheaterMenu(food, theater, false, amount);
            tm.addTheaterInEntity(theater);

            return tm;
        }
    }
}
