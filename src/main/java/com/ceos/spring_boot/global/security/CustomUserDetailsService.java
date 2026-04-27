package com.ceos.spring_boot.global.security;

import com.ceos.spring_boot.domain.user.entity.User;
import com.ceos.spring_boot.domain.user.repository.UserRepository;
import com.ceos.spring_boot.global.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        try {
            // 토큰 검증 시 (숫자 ID가 들어올 때)
            Long userId = Long.parseLong(identifier);
            return userRepository.findById(userId)
                    .map(CustomUserDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("ID로 사용자를 찾을 수 없습니다."));

        } catch (NumberFormatException e) {
            // 로그인 시 (이메일 문자열이 들어올 때)
            return userRepository.findByEmail(identifier)
                    .map(CustomUserDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("이메일로 사용자를 찾을 수 없습니다."));
        }
    }

    public CustomUserDetails loadUserByUserId(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND_ERROR.getMessage()));

        return new CustomUserDetails(user);
    }
}