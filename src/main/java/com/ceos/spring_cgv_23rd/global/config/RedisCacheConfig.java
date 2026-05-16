package com.ceos.spring_cgv_23rd.global.config;

import java.time.Duration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Configuration
@EnableCaching
public class RedisCacheConfig implements CachingConfigurer {

	@Bean
	public ObjectMapper cacheObjectMapper() {
		return JsonMapper.builder().build();
	}

	@Bean
	public RedisCacheConfiguration redisCacheConfiguration(
		ObjectMapper cacheObjectMapper) {

		return RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofHours(1))
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new JacksonJsonRedisSerializer<>(
					cacheObjectMapper,
					cacheObjectMapper.getTypeFactory().constructType(Object.class))));
	}

	@Bean
	public RedisCacheManager cacheManager(
		RedisConnectionFactory connectionFactory,
		RedisCacheConfiguration redisCacheConfiguration,
		ObjectProvider<RedisCacheManagerBuilderCustomizer> customizers) {

		RedisCacheManager.RedisCacheManagerBuilder builder =
			RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(redisCacheConfiguration);

		customizers.orderedStream().forEach(c -> c.customize(builder));

		return builder.build();
	}

	@Bean
	public CacheErrorHandler errorHandler() {
		return new SimpleCacheErrorHandler() {
			@Override
			public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
				log.warn("Cache GET error: cache={}, key={}, message={}", cache.getName(), key, ex.getMessage());
			}

			@Override
			public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
				log.error("Cache PUT error: cache={}, key={}, message={}", cache.getName(), key, ex.getMessage());
			}

			@Override
			public void handleCacheEvictError(RuntimeException ex, Cache cache, Object key) {
				log.error("Cache EVICT error: cache={}, key={}, message={}", cache.getName(), key, ex.getMessage());
			}

			@Override
			public void handleCacheClearError(RuntimeException ex, Cache cache) {
				log.error("Cache CLEAR error: cache={}, message={}", cache.getName(), ex.getMessage());
			}
		};

	}
}
