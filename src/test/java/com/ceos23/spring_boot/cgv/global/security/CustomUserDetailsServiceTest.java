package com.ceos23.spring_boot.cgv.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.ceos23.spring_boot.cgv.domain.user.User;
import com.ceos23.spring_boot.cgv.domain.user.UserRole;
import com.ceos23.spring_boot.cgv.global.cache.CacheNames;
import com.ceos23.spring_boot.cgv.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    void tearDown() {
        if (cacheManager.getCache(CacheNames.AUTH_USER_DETAILS) != null) {
            cacheManager.getCache(CacheNames.AUTH_USER_DETAILS).clear();
        }
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("user details are served from cache after the first lookup")
    void loadUserByUsername_usesCacheAfterFirstLookup() {
        User user = userRepository.save(new User("user1", "user1@example.com", "password", UserRole.USER));

        UserDetails firstLookup = customUserDetailsService.loadUserByUsername(String.valueOf(user.getId()));
        assertThat(cacheManager.getCache(CacheNames.AUTH_USER_DETAILS).get(String.valueOf(user.getId())))
                .isNotNull();

        userRepository.deleteAllInBatch();

        UserDetails secondLookup = customUserDetailsService.loadUserByUsername(String.valueOf(user.getId()));

        assertThat(secondLookup.getUsername()).isEqualTo(firstLookup.getUsername());
        assertThat(((CustomUserDetails) secondLookup).getEmail()).isEqualTo("user1@example.com");
    }
}
