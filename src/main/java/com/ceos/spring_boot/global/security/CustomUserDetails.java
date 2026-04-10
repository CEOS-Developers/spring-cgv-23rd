package com.ceos.spring_boot.global.security;

import com.ceos.spring_boot.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security의 표준 사용자 모델인 UserDetails를 구현한 클래스
 * 우리 프로젝트의 User 엔티티를 Spring Security가 인식할 수 있는 형태로 변환
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * 사용자가 가진 권한(Role) 목록을 반환
     * ex: ROLE_USER, ROLE_ADMIN
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name())); // 권한 이름을 가져와서 Security 권한 객체로 변환
    }

    @Override
    public String getUsername() { // user_id 반환
        return String.valueOf(user.getId());
    }

    @Override public String getPassword() { // 암호화된 비밀번호 반환
        return user.getPassword();
    }

    @Override public boolean isAccountNonExpired() { // 계정 만료 여부 반환
        return true;
    }

    @Override public boolean isAccountNonLocked() { // 계정 잠금 여부 반환
        return true;
    }

    @Override public boolean isCredentialsNonExpired() { // 자격 증명(비밀번호) 만료 여부를 반환
        return true;
    }

    @Override public boolean isEnabled() { // 계정 활성화 여부를 반환
        return true;
    }
}