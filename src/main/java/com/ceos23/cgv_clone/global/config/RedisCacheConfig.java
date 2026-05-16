package com.ceos23.cgv_clone.global.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(10))
			.disableCachingNullValues();

		return RedisCacheManager.builder(connectionFactory)
			.cacheDefaults(defaultConfig)
			.withInitialCacheConfigurations(Map.of(
				"movieDetail", cacheConfig(Duration.ofMinutes(10)),
				"theaterDetail", cacheConfig(Duration.ofMinutes(30)),
				"theatersByRegion", cacheConfig(Duration.ofMinutes(30)),
				"schedules", cacheConfig(Duration.ofMinutes(5))
			))
			.build();
	}

	private RedisCacheConfiguration cacheConfig(Duration ttl) {
		return RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(ttl)
			.disableCachingNullValues();
	}
}