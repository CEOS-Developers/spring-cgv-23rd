package com.ceos23.spring_boot.controller.movie.dto;

import com.ceos23.spring_boot.domain.movie.dto.MovieUpdateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Schema(description = "영화 정보 수정 요청 데이터")
public record MovieUpdateRequest(

        @Schema(description = "수정할 영화 제목", example = "인셉션2")
        @NotBlank(message = "영화 제목은 필수입니다.")
        String title,

        @Schema(description = "수정할 상영 시간(분)", example = "100")
        @NotNull(message = "상영 시간은 필수입니다.")
        @Positive(message = "상영 시간은 양수여야 합니다.")
        Integer runtime,

        @Schema(description = "수정할 개봉일", example = "2026-01-01")
        @NotNull(message = "개봉일은 필수입니다.")
        LocalDate releaseDate,

        @Schema(description = "수정할 관람 등급", example = "15세 이상 관람가")
        @NotBlank(message = "관람 등급은 필수입니다.")
        String ageRating,

        @Schema(description = "수정할 포스터 이미지 URL", example = "https://image.domain.com/poster/inception2.jpg")
        String posterUrl,

        @Schema(description = "수정할 영화 줄거리", example = "인셉션2는...")
        String description
) {
    // 🌟 Controller -> Service로 데이터를 넘길 때 사용하는 Command 변환 메서드
    public MovieUpdateCommand toCommand() {
        return new MovieUpdateCommand(title, runtime, releaseDate, ageRating, posterUrl, description);
    }
}
