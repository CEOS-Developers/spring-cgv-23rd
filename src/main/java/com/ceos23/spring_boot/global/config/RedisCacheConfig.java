package com.ceos23.spring_boot.global.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
public class RedisCacheConfig {

    private static final long TTL = TimeUnit.HOURS.toMillis(24);

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        return new RedissonSpringCacheManager(redissonClient, Map.of(
                "movies",     new CacheConfig(TTL, 0),
                "moviesAll",  new CacheConfig(TTL, 0),
                "theaters",   new CacheConfig(TTL, 0),
                "theatersAll", new CacheConfig(TTL, 0)
        ));
    }
}