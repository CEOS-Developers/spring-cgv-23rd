package com.ceos23.spring_cgv_23rd.Theater.DTO.Response;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Region;
import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import com.ceos23.spring_cgv_23rd.Theater.Domain.TheaterMenu;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public record TheaterWrapperDTO(
        long id,
        String name,
        Region region,
        String address
) {
    public static TheaterWrapperDTO create(Theater theater){
        return new TheaterWrapperDTO(
                theater.getId(),
                theater.getName(),
                theater.getRegion(),
                theater.getAddress()
        );
    }

    public static List<TheaterWrapperDTO> create(List<Theater> theaters){
        List<TheaterWrapperDTO> res = new ArrayList<>();

        for (Theater theater : theaters){
            res.add(TheaterWrapperDTO.create(theater));
        }

        return res;
    }
}