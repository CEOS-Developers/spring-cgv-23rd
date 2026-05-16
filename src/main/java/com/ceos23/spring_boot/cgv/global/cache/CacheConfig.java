package com.ceos23.spring_boot.cgv.global.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
                buildCache(CacheNames.AUTH_USER_DETAILS, Duration.ofMinutes(10), 1_000),
                buildCache(CacheNames.SCREENINGS, Duration.ofMinutes(3), 200),
                buildCache(CacheNames.SEAT_TEMPLATES, Duration.ofMinutes(30), 200),
                buildCache(CacheNames.STORE_MENUS, Duration.ofSeconds(60), 200)
        ));
        return cacheManager;
    }

    private CaffeineCache buildCache(String name, Duration ttl, long maximumSize) {
        return new CaffeineCache(
                name,
                Caffeine.newBuilder()
                        .expireAfterWrite(ttl)
                        .maximumSize(maximumSize)
                        .recordStats()
                        .build()
        );
    }
}
