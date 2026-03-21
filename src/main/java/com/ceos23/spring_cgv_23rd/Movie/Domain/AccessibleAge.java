package com.ceos23.spring_cgv_23rd.Movie.Domain;

public enum AccessibleAge {
    ALL(0), TWELVE(12), FIFTEEN(15), NINETEEN(19);

    private int age;

    AccessibleAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }
}
