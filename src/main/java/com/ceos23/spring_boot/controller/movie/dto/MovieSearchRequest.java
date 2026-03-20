package com.ceos23.spring_boot.controller.movie.dto;

import com.ceos23.spring_boot.domain.movie.dto.MovieSearchCommand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화 검색 조건 요청 DTO")
public record MovieSearchRequest(

        @Schema(description = "검색할 영화 제목 (입력하지 않으면 전체 조회)", example = "인셉")
        String title
) {
    public MovieSearchCommand toCommand() {
        return new MovieSearchCommand(title);
    }
}