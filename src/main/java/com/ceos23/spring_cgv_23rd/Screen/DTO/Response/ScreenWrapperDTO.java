package com.ceos23.spring_cgv_23rd.Screen.DTO.Response;

import com.ceos23.spring_cgv_23rd.Screen.Domain.CinemaType;
import com.ceos23.spring_cgv_23rd.Screen.Domain.Screening;

import java.util.List;

public record ScreenWrapperDTO(
        CinemaType cinemaType, List<ScreeningWrapperDTO> screening
) {
    public static ScreenWrapperDTO create(CinemaType cinemaType, List<ScreeningWrapperDTO> scd){
        return new ScreenWrapperDTO(
                cinemaType, scd
        );
    }
}
