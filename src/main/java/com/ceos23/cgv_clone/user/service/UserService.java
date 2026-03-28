package com.ceos23.cgv_clone.user.service;

import com.ceos23.cgv_clone.global.exception.CustomException;
import com.ceos23.cgv_clone.global.jwt.TokenProvider;
import com.ceos23.cgv_clone.global.response.ErrorCode;
import com.ceos23.cgv_clone.user.domain.User;
import com.ceos23.cgv_clone.user.dto.reponse.LoginResponse;
import com.ceos23.cgv_clone.user.dto.request.LoginRequest;
import com.ceos23.cgv_clone.user.dto.request.SignUpRequest;
import com.ceos23.cgv_clone.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        // userId로 유저 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // accessToken 발급
        String token = tokenProvider.createAccessToken(user.getId());

        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }

    public LoginResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        userRepository.save(user);

        String token = tokenProvider.createAccessToken(user.getId());

        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }
}
