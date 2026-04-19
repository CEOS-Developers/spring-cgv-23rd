package com.ceos23.cgv_clone.theater.dto.response;

import com.ceos23.cgv_clone.theater.entity.Theater;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TheaterResponse {
    private Long id;
    private String name;
    private String region;
    private String address;

    public static TheaterResponse from(Theater theater) {
        return TheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .region(theater.getRegion())
                .address(theater.getAddress())
                .build();
    }
}
