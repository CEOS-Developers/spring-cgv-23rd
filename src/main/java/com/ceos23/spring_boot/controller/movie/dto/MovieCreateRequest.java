package com.ceos23.spring_boot.controller.movie.dto;

import com.ceos23.spring_boot.domain.movie.dto.MovieCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Schema(description = "영화 생성 요청 DTO")
public record MovieCreateRequest(

        @Schema(description = "영화 제목", example = "인셉션")
        @NotBlank(message = "영화 제목은 필수입니다.")
        String title,

        @Schema(description = "상영 시간(분)", example = "120")
        @NotNull(message = "상영 시간은 필수입니다.")
        @Positive(message = "상영 시간은 양수여야 합니다.")
        Integer runtime,

        @Schema(description = "개봉일", example = "2000-01-01")
        @NotNull(message = "개봉일은 필수입니다.")
        LocalDate releaseDate,

        @Schema(description = "관람 등급", example = "12세 이상 관람가")
        @NotBlank(message = "관람 등급은 필수입니다.")
        String ageRating,

        @Schema(description = "포스터 이미지 URL", example = "https://image.domain.com/poster/inception.jpg")
        String posterUrl,

        @Schema(description = "영화 줄거리", example = "인셉션은...")
        String description
) {

    public MovieCreateCommand toCommand() {
        return new MovieCreateCommand(title, runtime, releaseDate, ageRating, posterUrl, description);
    }
}
