package com.ceos.spring_cgv_23rd.domain.auth.dto;

import lombok.Builder;

public class AuthResponseDTO {

    @Builder
    public record TokenResponseDTO(
            String accessToken,
            String refreshToken
    ) {
    }
}
