package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // protected로
public class Seat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY로 변경
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