package com.ceos23.spring_boot.cgv.global.security;

import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.global.exception.ErrorCode;
import com.ceos23.spring_boot.cgv.global.exception.NotFoundException;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Long userId = Long.parseLong(username);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}