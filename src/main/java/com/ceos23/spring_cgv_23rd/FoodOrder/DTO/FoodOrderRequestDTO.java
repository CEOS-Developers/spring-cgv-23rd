package com.ceos23.spring_cgv_23rd.FoodOrder.DTO;

import java.util.List;
import java.util.stream.Collectors;

public record FoodOrderRequestDTO(
        long theaterId,
        List<FoodMenuAndQuantityDTO> reqs
) {
}
