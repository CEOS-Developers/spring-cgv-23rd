package com.ceos23.spring_cgv_23rd.global.Filter;

import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserDetailsService의 구현체입니다.
 * UserLoginId를 받아서 UserDetails 객체로 반환합니다.
 *
 * UserDetails에서
 * Username - userLogId
 * Password - null
 * Authorities - authorities
 * 로 구성됩니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@NonNull String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return new org.springframework.security.core.userdetails.User(
                user.getLoginId(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
