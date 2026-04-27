package com.ceos23.spring_cgv_23rd.User.DTO;

import lombok.Getter;

public enum ROLE {
    USER ("USER"),
    ADMIN ("ADMIN");


    private String role;

    ROLE(String role) {
        this.role = role;
    }

    public String getAge() {
        return role;
    }
}
