package com.ceos.spring_cgv_23rd.global.config;

import com.ceos.spring_cgv_23rd.domain.user.domain.UserRole;
import com.ceos.spring_cgv_23rd.global.jwt.filter.JwtTokenFilter;
import com.ceos.spring_cgv_23rd.global.jwt.handler.JwtAccessDeniedHandler;
import com.ceos.spring_cgv_23rd.global.jwt.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                           JwtAccessDeniedHandler jwtAccessDeniedHandler) throws Exception {

        http
                // csrf 비활성화
                .csrf(csrf -> csrf.disable())
                // form login 비활성화
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasicAuth -> httpBasicAuth.disable())
                // cors 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션 stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인증/인가 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                // api 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/v1/auth/guest").permitAll()
                        .requestMatchers("/api/v1/auth/refresh").permitAll()
                        // guest
                        .requestMatchers("/api/v1/auth/**").hasRole(UserRole.GUEST.name())
                        .requestMatchers("/api/v1/reservations/guest/**").hasRole(UserRole.GUEST.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/movies/**", "/api/v1/products/**", "/api/v1/reviews/**", "/api/v1/screenings/**", "/api/v1/theaters/**", "/api/v1/reservations/**").hasRole(UserRole.GUEST.name())
                        //manager
                        .requestMatchers("/api/v1/manager/**").hasRole(UserRole.MANAGER.name())
                        // admin
                        .requestMatchers("/api/v1/admin/**").hasRole(UserRole.ADMIN.name())
                        // user
                        .anyRequest().hasRole(UserRole.USER.name())
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://localhost:5173"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = UserRole.ADMIN.getAuthority() + " > " +
                UserRole.MANAGER.getAuthority() + " > " +
                UserRole.USER.getAuthority() + " > " +
                UserRole.GUEST.getAuthority();

        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }
}
