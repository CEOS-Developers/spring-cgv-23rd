package com.ceos23.spring_cgv_23rd.Theater.DTO.Response;

import com.ceos23.spring_cgv_23rd.Theater.Domain.Theater;
import lombok.Builder;

@Builder
public record TheaterSearchResponseDTO(
        Theater theater
) {
}
