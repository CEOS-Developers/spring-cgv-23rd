package com.ceos23.cgv.global.cache;

import com.ceos23.cgv.domain.movie.entity.Movie;
import com.ceos23.cgv.domain.movie.enums.Genre;
import com.ceos23.cgv.domain.movie.enums.MovieRating;
import com.ceos23.cgv.domain.movie.repository.MovieRepository;
import com.ceos23.cgv.domain.movie.service.MovieService;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CachePerformanceTest {

    private static final int MOVIE_COUNT = 30;
    private static final int REPEAT_COUNT = 100;

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        clearMoviesCache();
        movieRepository.deleteAll();
        movieRepository.saveAll(IntStream.rangeClosed(1, MOVIE_COUNT)
                .mapToObj(index -> Movie.create(
                        "테스트 영화 " + index,
                        120,
                        LocalDate.of(2026, 1, 1),
                        MovieRating.ALL,
                        Genre.DRAMA,
                        "캐시 성능 측정용 영화"
                ))
                .toList());
    }

    @Test
    @DisplayName("영화 목록 조회 캐시 적용 후 반복 조회 응답 시간과 적중률을 측정한다")
    void measureMovieListCachePerformance() {
        long coldElapsedNs = measureSingleCallWithEmptyCache();

        clearMoviesCache();
        movieService.getAllMovies();

        long warmElapsedNs = measureRepeatedWarmCalls();
        CacheStats stats = moviesNativeCache().stats();

        double coldAvgMs = toMillis(coldElapsedNs);
        double warmAvgMs = toMillis(warmElapsedNs) / REPEAT_COUNT;
        double improvementRate = ((coldAvgMs - warmAvgMs) / coldAvgMs) * 100;

        System.out.printf(
                "CACHE_PERFORMANCE movieCount=%d coldAvgMs=%.3f warmAvgMs=%.3f improvementRate=%.2f%% hitCount=%d missCount=%d hitRate=%.2f%%%n",
                MOVIE_COUNT,
                coldAvgMs,
                warmAvgMs,
                improvementRate,
                stats.hitCount(),
                stats.missCount(),
                stats.hitRate() * 100
        );

        assertThat(stats.hitCount()).isEqualTo(REPEAT_COUNT);
        assertThat(stats.missCount()).isEqualTo(2);
        assertThat(stats.hitRate()).isGreaterThan(0.98);
        assertThat(warmAvgMs).isLessThan(coldAvgMs);
    }

    private long measureSingleCallWithEmptyCache() {
        long start = System.nanoTime();
        movieService.getAllMovies();
        return System.nanoTime() - start;
    }

    private long measureRepeatedWarmCalls() {
        long start = System.nanoTime();
        for (int index = 0; index < REPEAT_COUNT; index++) {
            movieService.getAllMovies();
        }
        return System.nanoTime() - start;
    }

    private void clearMoviesCache() {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache(CacheNames.MOVIES);
        if (cache != null) {
            cache.clear();
            cache.getNativeCache().policy().expireVariably();
        }
    }

    private com.github.benmanes.caffeine.cache.Cache<Object, Object> moviesNativeCache() {
        return (com.github.benmanes.caffeine.cache.Cache<Object, Object>)
                ((CaffeineCache) cacheManager.getCache(CacheNames.MOVIES)).getNativeCache();
    }

    private double toMillis(long elapsedNs) {
        return elapsedNs / 1_000_000.0;
    }
}
