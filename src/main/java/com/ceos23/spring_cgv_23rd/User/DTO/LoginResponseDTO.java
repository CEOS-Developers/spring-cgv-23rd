package com.ceos23.spring_cgv_23rd.User.DTO;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken
) {
    public static LoginResponseDTO create(String at, String rt){
        return new LoginResponseDTO(at, rt);
    }
}
