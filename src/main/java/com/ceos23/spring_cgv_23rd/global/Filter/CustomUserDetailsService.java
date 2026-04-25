package com.ceos23.spring_cgv_23rd.global.Filter;

import com.ceos23.spring_cgv_23rd.User.Domain.User;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return new org.springframework.security.core.userdetails.User(
                user.getLoginId(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
