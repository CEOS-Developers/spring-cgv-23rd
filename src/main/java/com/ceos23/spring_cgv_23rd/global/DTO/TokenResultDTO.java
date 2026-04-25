package com.ceos23.spring_cgv_23rd.global.DTO;

public record TokenResultDTO(
        String accessToken,
        String refreshToken
) {
    public static TokenResultDTO create(String accessToken, String refreshToken){
        return new TokenResultDTO(accessToken, refreshToken);
    }
}
