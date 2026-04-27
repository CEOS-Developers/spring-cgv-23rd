package com.ceos23.spring_cgv_23rd.Theater.DTO.Response;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;

import java.util.List;

public record MenuFindingResponseDTO(
        long theaterId,
        String theaterName,
        List<MenuWrapperDTO> menus
) {
    public static MenuFindingResponseDTO create(Theater theater, List<MenuWrapperDTO> menus){
        return new MenuFindingResponseDTO(
                theater.getId(),
                theater.getName(),
                menus
        );

    }
}
