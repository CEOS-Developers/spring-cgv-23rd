package com.ceos23.spring_cgv_23rd.Token.DTO;

public record ExpiredTokenResponseDTO(
        int code,
        String accessToken,
        String refreshToken
) {
    public static ExpiredTokenResponseDTO create(
            int code,
            String accessToken,
            String refreshToken
    ){
        return new ExpiredTokenResponseDTO(code, accessToken, refreshToken);
    }
}
