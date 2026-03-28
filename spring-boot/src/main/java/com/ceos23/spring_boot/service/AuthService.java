package com.ceos23.spring_boot.service;

import com.ceos23.spring_boot.domain.User;
import com.ceos23.spring_boot.dto.LoginRequest;
import com.ceos23.spring_boot.dto.LoginResponse;
import com.ceos23.spring_boot.dto.SignUpRequest;
import com.ceos23.spring_boot.dto.SignUpResponse;
import com.ceos23.spring_boot.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.jwt.TokenProvider;
import com.ceos23.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponse signUp(SignUpRequest request) {
        validateSignUpRequest(request);

        User user = User.of(
                request.getName(),
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        return new SignUpResponse(savedUser.getId(), savedUser.getName());
    }

    public LoginResponse login(LoginRequest request) {
        validateLoginRequest(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                String.valueOf(user.getId()),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String accessToken = tokenProvider.createAccessToken(user.getId(), authentication);

        return new LoginResponse(accessToken);
    }

    private void validateSignUpRequest(SignUpRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("회원가입 요청은 비어 있을 수 없습니다.");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("로그인 요청은 비어 있을 수 없습니다.");
        }

        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new IllegalArgumentException("userId는 1 이상이어야 합니다.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }
}