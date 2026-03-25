package com.ceos23.cgv_clone.user.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.jwt.TokenProvider;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.user.dto.reponse.LoginResponse;
import com.ceos23.cgv_clone.user.dto.request.LoginRequest;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public LoginResponse login(LoginRequest request) {
        // userId로 유저 확인
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // accessToken 발급
        String token = tokenProvider.createAccessToken(request.getUserId());

        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }
}
