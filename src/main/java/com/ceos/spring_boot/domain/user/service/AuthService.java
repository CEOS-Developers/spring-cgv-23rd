package com.ceos.spring_boot.domain.user.service;

import com.ceos.spring_boot.domain.user.dto.AuthRequest;
import com.ceos.spring_boot.domain.user.dto.AuthResponse;
import com.ceos.spring_boot.domain.user.dto.SignupRequest;
import com.ceos.spring_boot.domain.user.dto.SignupResponse;
import com.ceos.spring_boot.domain.user.entity.UserRole;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.domain.user.repository.UserRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(ErrorCode.DUPLICATE_EMAIL_ERROR.getMessage());
        }

        // 비밀번호 암호화 및 유저 생성
        User user = User.builder()
                .name(request.name())
                .nickname(request.nickname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // 암호화
                .phone(request.phoneNumber())
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        return SignupResponse.of(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                user.getPhone()
        );
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {

        // 이메일로 유저 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND_ERROR.getMessage()));

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException(ErrorCode.INVALID_LOGIN_ERROR.getMessage());
        }

        // 인증 객체 생성 및 토큰 발급 (Spring Security의 인증 과정을 거쳐 권한 정보를 토큰에 담기)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getId(), request.password());

        // 실제 검증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 토큰 생성 및 반환
        String jwt = tokenProvider.createAccessToken(user.getId(), authentication);
        return AuthResponse.of(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                jwt
        );
    }
}
