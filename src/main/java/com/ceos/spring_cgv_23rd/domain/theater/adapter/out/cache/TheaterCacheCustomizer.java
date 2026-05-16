package com.ceos.spring_cgv_23rd.domain.theater.adapter.out.cache;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;

import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.TheaterDetailResult;
import com.ceos.spring_cgv_23rd.domain.theater.application.dto.result.TheaterResult;
import com.ceos.spring_cgv_23rd.global.config.RedisCacheConfig;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

@Component
@RequiredArgsConstructor
public class TheaterCacheCustomizer implements RedisCacheManagerBuilderCustomizer {

	private final RedisCacheConfig.CacheConfigurationFactory configFactory;

	@Override
	public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
		TypeFactory typeFactory = configFactory.typeFactory();

		JavaType theaterListType = typeFactory.constructCollectionType(List.class, TheaterResult.class);
		JavaType theaterDetailType = typeFactory.constructType(TheaterDetailResult.class);

		builder
			.withCacheConfiguration("theater:list", configFactory.create(Duration.ofHours(6), theaterListType))
			.withCacheConfiguration("theater:detail", configFactory.create(Duration.ofHours(6), theaterDetailType));
	}

}
