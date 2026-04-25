package com.ceos23.spring_cgv_23rd.FoodOrder.DTO;

import java.util.List;

public record FoodOrderRequestDTO(
        long theaterId,
        List<FoodMenuAndQuantityDTO> reqs
) {
}
