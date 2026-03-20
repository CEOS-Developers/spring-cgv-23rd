package com.ceos23.cgv.domain.concession.dto;

import com.ceos23.cgv.domain.concession.entity.Product;
import com.ceos23.cgv.domain.concession.enums.ProductCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {
    private Long productId;
    private String name;
    private int price;
    private String description;
    private ProductCategory category;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .category(product.getCategory())
                .build();
    }
}