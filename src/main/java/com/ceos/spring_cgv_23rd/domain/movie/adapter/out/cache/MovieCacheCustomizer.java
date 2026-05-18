package com.ceos.spring_cgv_23rd.domain.movie.adapter.out.cache;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;

import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieCreditResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieDetailResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieMediaResult;
import com.ceos.spring_cgv_23rd.domain.movie.application.dto.result.MovieResult;
import com.ceos.spring_cgv_23rd.global.config.RedisCacheConfig;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

@Component
@RequiredArgsConstructor
public class MovieCacheCustomizer implements RedisCacheManagerBuilderCustomizer {

	private final RedisCacheConfig.CacheConfigurationFactory configFactory;

	@Override
	public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
		TypeFactory typeFactory = configFactory.typeFactory();

		JavaType movieListType = typeFactory.constructCollectionType(List.class, MovieResult.class);
		JavaType movieDetailType = typeFactory.constructType(MovieDetailResult.class);
		JavaType creditListType = typeFactory.constructCollectionType(List.class, MovieCreditResult.class);
		JavaType mediaListType = typeFactory.constructCollectionType(List.class, MovieMediaResult.class);

		builder
			.withCacheConfiguration("movie:chart", configFactory.create(Duration.ofMinutes(10), movieListType))
			.withCacheConfiguration("movie:running", configFactory.create(Duration.ofMinutes(10), movieListType))
			.withCacheConfiguration("movie:upcoming", configFactory.create(Duration.ofMinutes(10), movieListType))
			.withCacheConfiguration("movie:detail", configFactory.create(Duration.ofMinutes(10), movieDetailType))

			.withCacheConfiguration("movie:credits", configFactory.create(Duration.ofHours(6), creditListType))
			.withCacheConfiguration("movie:medias", configFactory.create(Duration.ofHours(6), mediaListType));
	}

}
