package com.ceos23.spring_cgv_23rd.Theater.Domain;

import com.ceos23.spring_cgv_23rd.Media.Domain.Media;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food {
    private Food(String menuName, int price){
        this.menuName = menuName;
        this.price = price;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String menuName;

    private int price;

    @OneToMany
    @JoinColumn(name = "food_photos")
    private List<Media> foodPhotos = new ArrayList<>();

    public void addFoodPhotos(List<Media> photos){
        foodPhotos.addAll(photos);
    }

    public static Food create(String menuName, int price){
        return new Food(menuName, price);
    }

    public static Food create(String menuName, int price, List<Media> photos){
        Food food = new Food(menuName, price);
        food.addFoodPhotos(photos);
        return food;
    }
}
