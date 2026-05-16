package com.ceos.spring_cgv_23rd.global.config;

import java.time.Duration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.EqualJitterDelay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://" + host + ":" + port)
			.setConnectTimeout(1000)
			.setTimeout(1000)
			.setRetryAttempts(3)
			.setRetryDelay(new EqualJitterDelay(Duration.ofMillis(200), Duration.ofMillis(500)));
		return Redisson.create(config);
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {

		SocketOptions socketOptions = SocketOptions.builder()
			.connectTimeout(Duration.ofMillis(500))
			.keepAlive(true)
			.build();

		ClientOptions clientOptions = ClientOptions.builder()
			.socketOptions(socketOptions)
			.disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
			.timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(1)))
			.autoReconnect(true)
			.suspendReconnectOnProtocolFailure(true)
			.build();

		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
			.commandTimeout(Duration.ofSeconds(1))
			.shutdownTimeout(Duration.ofMillis(100))
			.clientOptions(clientOptions)
			.build();

		RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(host, port);

		return new LettuceConnectionFactory(serverConfig, clientConfig);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}
}
