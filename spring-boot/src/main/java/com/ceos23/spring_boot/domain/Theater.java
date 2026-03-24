package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Theater {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String location;

    public Theater(String name, String location) {
        this.name = name;
        this.location = location;
    }
}