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

    // 엔티티가 아닌 필요한 필드만 가져와 최적화
    private final Long id;
    private final String password;
    private final String role;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.password = user.getPassword();
        this.role = user.getRole().name();
    }

    /**
     * 필드로 저장된 role 문자열을 바탕으로 권한 객체 생성
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public String getPassword() {
        return password;
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