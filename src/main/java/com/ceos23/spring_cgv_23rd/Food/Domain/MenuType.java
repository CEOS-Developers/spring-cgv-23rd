package com.ceos23.spring_cgv_23rd.Food.Domain;

public enum MenuType {
    NEW("신제품"),
    COMBO("콤보"),
    POPCORN("팝콘"),
    BEVERAGE("음료"),
    SNACK("스낵"),
    CHARACTER("캐릭터굿즈");

    private String typeName;

    MenuType(String typeName){
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
