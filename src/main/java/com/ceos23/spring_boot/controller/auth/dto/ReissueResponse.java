package com.ceos23.spring_boot.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AccessToken 및 RefreshToken 재발급 성공 응답 DTO")
public record ReissueResponse(

        @Schema(description = "발급된 Access Token", example = "agwegadfewfawefaw")
        String accessToken
) {}