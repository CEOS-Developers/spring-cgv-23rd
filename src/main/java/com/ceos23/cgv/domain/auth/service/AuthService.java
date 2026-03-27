package com.ceos23.cgv.domain.auth.service;

import com.ceos23.cgv.domain.auth.dto.LoginRequest;
import com.ceos23.cgv.domain.auth.dto.SignupRequest;
import com.ceos23.cgv.domain.auth.dto.TokenResponse;
import com.ceos23.cgv.domain.auth.dto.UserResponse;
import com.ceos23.cgv.domain.user.entity.User;
import com.ceos23.cgv.domain.user.enums.Role;
import com.ceos23.cgv.domain.user.repository.UserRepository;
import com.ceos23.cgv.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    /**
     * 회원가입 로직
     */
    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 가입되어 있는 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname());
    }

    /**
     * 로그인 및 토큰 발급 로직
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. Login 이메일/비밀번호를 기반으로 Authentication 객체 생성
        // 이때 authentication은 인증 여부를 확인하는 authenticated 값이 false인 상태입니다.
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // 2. 실제 비밀번호 검증이 이루어지는 부분
        // authenticate 메서드가 실행될 때 CustomUserDetailsService의 loadUserByUsername 메서드가 실행됩니다.
        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        // (저희는 TokenProvider에서 ID값을 기반으로 토큰을 만들도록 설계했습니다)
        Long userId = Long.parseLong(authentication.getName());
        String accessToken = tokenProvider.createAccessToken(userId, authentication);

        // 4. 발급된 토큰을 Response DTO에 담아 반환
        return new TokenResponse(accessToken);
    }
}