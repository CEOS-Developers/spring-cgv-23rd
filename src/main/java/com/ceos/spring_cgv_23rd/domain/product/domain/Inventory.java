package com.ceos.spring_cgv_23rd.domain.product.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Inventory {


    private Long id;
    private Long theaterId;
    private Long productId;
    private Integer quantity;

}
