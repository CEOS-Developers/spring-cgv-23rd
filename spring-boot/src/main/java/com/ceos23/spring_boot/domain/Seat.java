package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Seat {

    @Id @GeneratedValue
    private Long id;

    private int rowNum;
    private int colNum;

    @ManyToOne
    private Screen screen;

    public Seat(int rowNum, int colNum, Screen screen) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.screen = screen;
    }
}