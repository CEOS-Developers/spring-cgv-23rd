package com.ceos23.spring_cgv_23rd.Theater.DTO.Response;

import lombok.Builder;

import java.util.List;

@Builder
public record TheaterSearchResponseDTO(
        List<TheaterWrapperDTO> theater
) {
}
