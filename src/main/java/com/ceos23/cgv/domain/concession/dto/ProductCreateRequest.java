package com.ceos23.cgv.domain.concession.dto;

import com.ceos23.cgv.domain.concession.enums.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {
    private String name;
    private int price;
    private String description;
    private String origin;
    private String ingredient;
    private Boolean pickupPossible;
    private ProductCategory category;
}