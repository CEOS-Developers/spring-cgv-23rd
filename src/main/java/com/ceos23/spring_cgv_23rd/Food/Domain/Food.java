package com.ceos23.spring_cgv_23rd.Food.Domain;

import com.ceos23.spring_cgv_23rd.Media.Domain.Media;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food {
    private Food(String menuName, int price, String description, MenuType menuType){
        this.foodName = menuName;
        this.price = price;
        this.description = description;
        this.menuType = menuType;
    }

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    private MenuType menuType;

    private String description;

    @Getter
    private String foodName;

    @Getter
    private int price;

    @OneToMany
    @JoinColumn(name = "food_photos")
    private List<Media> foodPhotos = new ArrayList<>();

    public void addFoodPhotos(List<Media> photos){
        foodPhotos.addAll(photos);
    }

    public static Food create(String menuName, int price, String description, MenuType menuType){
        return new Food(menuName, price, description, menuType);
    }

    public static Food create(String menuName, int price, String description, MenuType menuType, List<Media> photos){
        Food food = new Food(menuName, price, description, menuType);
        food.addFoodPhotos(photos);
        return food;
    }
}
