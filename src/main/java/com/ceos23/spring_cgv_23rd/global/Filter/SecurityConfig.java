package com.ceos23.spring_cgv_23rd.global.Filter;

import com.ceos23.spring_cgv_23rd.Token.Service.TokenProvider;
import com.ceos23.spring_cgv_23rd.User.Repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter(TokenProvider tokenProvider) {
        return new JWTAuthenticationFilter(tokenProvider);
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http,
                                            JWTAuthenticationFilter jwtAuthenticationFilter,
                                            CustomLogoutHandler customLogoutHandler,
                                            CustomLogoutSuccessHandler successHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/api/menus",
                                "/api/theater",
                                "/api/movie",
                                "/api/screen"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST
                        ).permitAll()
                        .requestMatchers(
                                "/",
                                "/css/**", "/images/**", "/favicon.ico/**",
                                "/api/login/**",
                                "/api/signup"
                        ).permitAll()
                        .anyRequest().authenticated())
/*
                        .requestMatchers(HttpMethod.POST, "/api/login", "/api/login/**", "/api/signup").permitAll()
                        .requestMatchers("/api/movie/likes", "/api/theater/likes").authenticated()
                        .requestMatchers(
                                "/", "/css/**", "/js/**", "/images/**", "/favicon.ico/**",
                                "/h2-console/**", "/oauth2/**",
                                "/api/orders",
                                "/api/movie/**",
                                "/api/screen/**",
                                "/api/theater/**",
                                "/api/signup/**",
                                "/api/menus",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()

 */
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .csrf(AbstractHttpConfigurer::disable)
                //테스트 끝나고 켜놓기

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler(successHandler)
                );

        return http.build();
    }

}
