package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.cache;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieCreditResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieDetailResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieMediaResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieResult;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.TypeFactory;

@Component
@RequiredArgsConstructor
public class MovieCacheCustomizer implements RedisCacheManagerBuilderCustomizer {

	private final ObjectMapper cacheObjectMapper;

	@Override
	public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
		TypeFactory typeFactory = cacheObjectMapper.getTypeFactory();

		JavaType movieListType = typeFactory.constructCollectionType(List.class, MovieResult.class);
		JavaType movieDetailType = typeFactory.constructType(MovieDetailResult.class);
		JavaType creditListType = typeFactory.constructCollectionType(List.class, MovieCreditResult.class);
		JavaType mediaListType = typeFactory.constructCollectionType(List.class, MovieMediaResult.class);

		builder
			.withCacheConfiguration("movie:chart", cacheConfig(Duration.ofMinutes(10), movieListType))
			.withCacheConfiguration("movie:running", cacheConfig(Duration.ofMinutes(10), movieListType))
			.withCacheConfiguration("movie:upcoming", cacheConfig(Duration.ofMinutes(10), movieListType))
			.withCacheConfiguration("movie:detail", cacheConfig(Duration.ofMinutes(10), movieDetailType))

			.withCacheConfiguration("movie:credits", cacheConfig(Duration.ofHours(6), creditListType))
			.withCacheConfiguration("movie:medias", cacheConfig(Duration.ofHours(6), mediaListType));
	}

	private RedisCacheConfiguration cacheConfig(Duration ttl, JavaType valueType) {
		return RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(ttl)
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new JacksonJsonRedisSerializer<>(cacheObjectMapper, valueType)));
	}
}
