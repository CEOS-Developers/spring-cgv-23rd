package com.ceos23.cgv.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CinetalkCreateRequest {
    private Long userId;
    private String title;
    private String content;

    // 0 or 1 관계이므로 값이 안 들어오면 null
    private Long movieId;
    private Long cinemaId;
}