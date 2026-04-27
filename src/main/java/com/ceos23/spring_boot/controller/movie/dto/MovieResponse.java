package com.ceos23.spring_boot.controller.movie.dto;

import com.ceos23.spring_boot.domain.movie.dto.MovieInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "영화 상세 응답 데이터")
public record MovieResponse(
        @Schema(description = "영화 고유 ID", example = "1")
        Long id,

        @Schema(description = "영화 제목", example = "인셉션")
        String title,

        @Schema(description = "상영 시간(분)", example = "120")
        Integer runtime,

        @Schema(description = "개봉일", example = "2000-01-01")
        LocalDate releaseDate,

        @Schema(description = "관람 등급", example = "12세 이상 관람가")
        String ageRating,

        @Schema(description = "평균 평점 (리뷰 없으면 null)", example = "9.9")
        BigDecimal averageRating,

        @Schema(description = "포스터 이미지 URL (없으면 null)", example = "https://image.domain.com/poster/inception.jpg")
        String posterUrl,

        @Schema(description = "영화 줄거리 (없으면 null)", example = "인셉션은...")
        String description
) {

    public static MovieResponse from(MovieInfo info) {
        return new MovieResponse(
                info.id(),
                info.title(),
                info.runtime(),
                info.releaseDate(),
                info.ageRating(),
                info.averageRating(),
                info.posterUrl(),
                info.description()
        );
    }
}
