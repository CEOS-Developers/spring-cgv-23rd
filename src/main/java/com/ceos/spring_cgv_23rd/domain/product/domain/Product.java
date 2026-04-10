package com.ceos.spring_cgv_23rd.domain.product.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private String description;
    private ProductCategory category;
    private Integer price;
    private String productImageUrl;
}
