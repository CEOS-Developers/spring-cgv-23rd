package com.ceos23.spring_cgv_23rd.Theater.DTO.Response;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import lombok.Builder;

import java.util.List;

@Builder
public record TheaterSearchAllResponseDTO(
        List<Theater> searchedTheaters
) {
}
