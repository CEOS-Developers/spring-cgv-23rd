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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findById(Long.parseLong(username))
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
    }

    public CustomUserDetails loadUserByUserId(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND_ERROR.getMessage()));

        return new CustomUserDetails(user);
    }
}