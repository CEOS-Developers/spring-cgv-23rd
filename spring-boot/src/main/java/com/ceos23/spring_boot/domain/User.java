package com.ceos23.spring_boot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue
    private Long id;

    private String name;

    public User(String name) {
        this.name = name;
    }
}