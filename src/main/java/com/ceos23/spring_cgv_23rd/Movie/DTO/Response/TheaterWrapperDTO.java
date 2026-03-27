package com.ceos23.spring_cgv_23rd.Movie.DTO.Response;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Region;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;

import java.util.ArrayList;
import java.util.List;

public record TheaterWrapperDTO(
        long id, String name
) {
    public static TheaterWrapperDTO create(Theater theater){
        return new TheaterWrapperDTO(
                theater.getId(),
                theater.getName()
        );
    }

    public static List<TheaterWrapperDTO> create(List<Theater> theaters){
        List<TheaterWrapperDTO> ts = new ArrayList<>();

        for (Theater t : theaters){
            ts.add(TheaterWrapperDTO.create(t));
        }

        return ts;
    }
}
