package com.ceos.spring_cgv_23rd.domain.theater.dto;

import com.ceos.spring_cgv_23rd.domain.theater.entity.Theater;
import lombok.Builder;

public class TheaterResponseDTO {

    @Builder
    public record TheaterListResponseDTO(
            Long id,
            String name,
            String address,
            boolean isOpened
    ) {
        public static TheaterListResponseDTO from(Theater theater) {
            return TheaterListResponseDTO.builder()
                    .id(theater.getId())
                    .name(theater.getName())
                    .address(theater.getAddress())
                    .isOpened(theater.isOpened())
                    .build();
        }
    }


    @Builder
    public record TheaterDetailResponseDTO(
            Long id,
            String name,
            String address,
            String description,
            boolean isOpened
    ) {
        public static TheaterDetailResponseDTO from(Theater theater) {
            return TheaterDetailResponseDTO.builder()
                    .id(theater.getId())
                    .name(theater.getName())
                    .address(theater.getAddress())
                    .description(theater.getDescription())
                    .isOpened(theater.isOpened())
                    .build();
        }
    }

    @Builder
    public record TheaterLikeResponseDTO(
            Long id,
            Boolean isLiked
    ) {
        public static TheaterLikeResponseDTO of(Long theaterId, Boolean isLiked) {
            return TheaterLikeResponseDTO.builder()
                    .id(theaterId)
                    .isLiked(isLiked)
                    .build();
        }
    }

}
