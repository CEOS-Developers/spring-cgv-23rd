package com.ceos.spring_cgv_23rd.global.security;

import com.ceos.spring_cgv_23rd.domain.user.domain.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class AuthUserDetails implements UserDetails {

    private final Long userId;
    private final String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        UserRole userRole = UserRole.valueOf(role);

        return List.of(new SimpleGrantedAuthority(userRole.getAuthority()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId != null ? userId.toString() : UserRole.GUEST.name();
    }
}
