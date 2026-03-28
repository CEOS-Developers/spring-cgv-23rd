package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.dto.LoginRequest;
import com.ceos23.spring_boot.dto.LoginResponse;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.jwt.TokenProvider;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public LoginResponse login(LoginRequest request) {
        validateLoginRequest(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                String.valueOf(user.getId()),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = tokenProvider.createAccessToken(user.getId(), authentication);

        return new LoginResponse(accessToken);
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null || request.getUserId() == null || request.getUserId() <= 0) {
            throw new IllegalArgumentException("userId는 1 이상이어야 합니다.");
        }
    }
}