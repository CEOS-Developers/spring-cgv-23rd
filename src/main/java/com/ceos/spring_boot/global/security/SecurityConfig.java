package com.ceos.spring_boot.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    // 비밀번호 암호화 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 사용
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 설정 (JWT를 사용하므로 비활성화)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 설정 (Stateless하게 관리)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 토큰 인증이므로 서버가 세션을 생성하거나 유지하지 않도록 설정


                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth

                                .anyRequest().permitAll()

                        /*
                        // 로그인, 회원가입, 스웨거 등은 토큰 없이도 접근 허용
                        .requestMatchers("/users/login", "/users/signup").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/api-docs/**","/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 관리자(Admin)만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/schedule/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/cinema/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/cinema/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/movie/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/movie/**").hasRole("ADMIN")


                        .anyRequest().authenticated() // 그 외 모든 요청은 반드시 인증 필요

                         */
                )

                // JWT 필터 추가 (UsernamePasswordAuthenticationFilter보다 먼저 실행되도록 설정)
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
