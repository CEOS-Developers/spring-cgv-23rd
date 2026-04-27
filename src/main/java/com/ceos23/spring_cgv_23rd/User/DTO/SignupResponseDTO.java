package com.ceos23.spring_cgv_23rd.User.DTO;

public record SignupResponseDTO (
        String accessToken,
        String refreshToken,
        UserWrapperDTO userWrapperDTO
) {
    public static SignupResponseDTO create(String accessToken, String refreshToken, UserWrapperDTO user){
        return new SignupResponseDTO(accessToken, refreshToken, user);
    }
}
