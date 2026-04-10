package com.cgv.spring_boot.domain.user.service;

import com.cgv.spring_boot.domain.user.entity.User;
import com.cgv.spring_boot.domain.user.entity.UserRole;
import com.cgv.spring_boot.domain.user.repository.UserRepository;
import com.cgv.spring_boot.global.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
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
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return createAuthenticatedUser(user);
    }

    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return createAuthenticatedUser(user);
    }

    private UserDetails createAuthenticatedUser(User user) {
        UserRole role = user.getRole() == null ? UserRole.USER : user.getRole();

        return new AuthenticatedUser(
                user.getId(),
                user.getLoginId(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
    }
}
