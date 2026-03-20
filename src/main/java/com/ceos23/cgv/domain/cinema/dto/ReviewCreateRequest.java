package com.ceos23.cgv.domain.cinema.dto;

import com.ceos23.cgv.domain.cinema.enums.TheaterType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {
    private Long userId;        // 작성자
    private Long movieId;       // 어떤 영화에 대한 리뷰인지
    private TheaterType type;   // 어느 상영관에서 봤는지 (예: IMAX, NORMAL)
    private String content;     // 리뷰 내용
}