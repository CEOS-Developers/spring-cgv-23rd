package com.ceos.spring_cgv_23rd.domain.auth.service;

import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthRequestDTO;
import com.ceos.spring_cgv_23rd.domain.auth.dto.AuthResponseDTO;
import com.ceos.spring_cgv_23rd.domain.user.entity.User;
import com.ceos.spring_cgv_23rd.domain.user.enums.UserRole;
import com.ceos.spring_cgv_23rd.domain.user.repository.UserRepository;
import com.ceos.spring_cgv_23rd.global.apiPayload.code.GeneralErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import com.ceos.spring_cgv_23rd.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponseDTO.TokenResponseDTO issueGuestToken() {

        // accessToken 및 refreshToken 생성
        String accessToken = jwtTokenProvider.generateGuestAccessToken();
        String refreshToken = jwtTokenProvider.generateGuestRefreshToken();

        return new AuthResponseDTO.TokenResponseDTO(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponseDTO.TokenResponseDTO signup(AuthRequestDTO.SignupRequestDTO request) {
        // 아이디 중복 검사
        if (userRepository.existsByUsername(request.username())) {
            throw new GeneralException(GeneralErrorCode.DUPLICATE_LOGINID);
        }

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            throw new GeneralException(GeneralErrorCode.DUPLICATE_EMAIL);
        }

        // 전화번호 중복 검사
        if (userRepository.existsByPhone(request.phone())) {
            throw new GeneralException(GeneralErrorCode.DUPLICATE_PHONE);
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.nickname())) {
            throw new GeneralException(GeneralErrorCode.DUPLICATE_NICKNAME);
        }


        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .birth(request.birth())
                .nickname(request.nickname())
                .gender(request.gender())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        return generateTokens(user);
    }

    @Override
    public AuthResponseDTO.TokenResponseDTO login(AuthRequestDTO.LoginRequestDTO request) {

        // 유저 조회
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.INVALID_LOGIN));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new GeneralException(GeneralErrorCode.INVALID_LOGIN);
        }

        return generateTokens(user);
    }


    private AuthResponseDTO.TokenResponseDTO generateTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return new AuthResponseDTO.TokenResponseDTO(accessToken, refreshToken);
    }
}
