package cgv_23rd.ceos.global.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return baseConfiguration(Duration.ofMinutes(10));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration("movieList", baseConfiguration(Duration.ofMinutes(5)))
                .withCacheConfiguration("movieDetail", baseConfiguration(Duration.ofMinutes(30)))
                .withCacheConfiguration("movieActors", baseConfiguration(Duration.ofMinutes(30)))
                .withCacheConfiguration("theatersByRegion", baseConfiguration(Duration.ofMinutes(30)))
                .withCacheConfiguration("theaterDetail", baseConfiguration(Duration.ofMinutes(30)))
                .withCacheConfiguration("schedules", baseConfiguration(Duration.ofMinutes(3)))
                .withCacheConfiguration("movieReviews", baseConfiguration(Duration.ofMinutes(1)));
    }

    private RedisCacheConfiguration baseConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .entryTtl(ttl);
    }
}
