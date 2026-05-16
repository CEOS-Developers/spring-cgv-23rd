package com.ceos.spring_boot.domain.user.service;

import com.ceos.spring_boot.domain.user.dto.AuthRequest;
import com.ceos.spring_boot.domain.user.dto.AuthResponse;
import com.ceos.spring_boot.domain.user.dto.SignupRequest;
import com.ceos.spring_boot.domain.user.dto.SignupResponse;
import com.ceos.spring_boot.domain.user.entity.UserRole;
import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.domain.user.repository.UserRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import com.ceos.spring_boot.global.exception.BusinessException;
import com.ceos.spring_boot.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        log.info("[Signup Attempt] 회원가입 시도 - email: {}", request.email());

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.email())) {
            log.warn("[Signup Failed] 이메일 중복 가입 시도 - email: {}", request.email());
            throw new IllegalArgumentException(ErrorCode.DUPLICATE_EMAIL_ERROR.getMessage());
        }

        // 비밀번호 암호화 및 유저 생성
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.create(
                request.name(),
                request.nickname(),
                request.email(),
                encodedPassword,
                request.phoneNumber()
        );

        log.info("[AUDIT - Signup Completed] 신규 회원 가입 완료 - userId: {}, email: {}",
                user.getId(), user.getEmail());

        userRepository.save(user);
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

        log.info("[Login Attempt] 로그인 시도 - email: {}", request.email());

        // 이메일로 유저 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("[SECURITY - Login Failed] 미가입 계정 로그인 시도 - email: {}", request.email());
                    return new BusinessException(ErrorCode.USER_NOT_FOUND_ERROR);
                });


        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("[SECURITY - Login Failed] 잘못된 비밀번호 입력 - email: {}", request.email());
            throw new BusinessException(ErrorCode.INVALID_LOGIN_ERROR);
        }

        // 인증 객체 생성 및 토큰 발급 (Spring Security의 인증 과정을 거쳐 권한 정보를 토큰에 담기)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // 실제 검증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        log.info("[AUDIT - Login Success] 로그인 성공 및 JWT 발급 완료 - userId: {}, email: {}",
                user.getId(), user.getEmail());

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
