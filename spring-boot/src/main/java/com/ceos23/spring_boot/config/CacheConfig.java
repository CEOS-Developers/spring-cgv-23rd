package com.ceos23.spring_boot.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCache movieAllCache = new CaffeineCache(
                "movieAll",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .maximumSize(1)
                        .build()
        );

        CaffeineCache movieCache = new CaffeineCache(
                "movie",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .maximumSize(100)
                        .build()
        );

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(movieAllCache, movieCache));

        return cacheManager;
    }
}