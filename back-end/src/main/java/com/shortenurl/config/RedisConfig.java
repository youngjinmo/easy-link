package com.shortenurl.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.timeout:2000}")
    private long timeout;

    @Value("${spring.data.redis.lettuce.pool.max-active:8}")
    private int maxActive;

    @Value("${spring.data.redis.lettuce.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.data.redis.lettuce.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.data.redis.lettuce.pool.max-wait:1000}")
    private long maxWait;

    private final Environment environment;

    public RedisConfig(Environment environment) {
        this.environment = environment;
    }

    private boolean isProductionProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis 서버 설정
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        if (!redisPassword.isEmpty()) {
            redisConfig.setPassword(redisPassword);
        }

        // Connection Pooling 설정
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(Duration.ofMillis(maxWait));
        
        // JMX 모니터링 활성화
        poolConfig.setJmxEnabled(true);
        poolConfig.setJmxNamePrefix("redis-pool");
        
        // 테스트 설정
        poolConfig.setTestOnBorrow(true);    // 커넥션 획득 전 테스트
        poolConfig.setTestOnReturn(true);     // 커넥션 반환 전 테스트
        poolConfig.setTestWhileIdle(true);    // 유휴 상태의 커넥션 테스트
        
        // Lettuce 클라이언트 설정
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = 
            LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(timeout))
                .poolConfig(poolConfig);

        // 운영 환경("prod")일 때만 SSL 적용
        if (isProductionProfile()) {
            builder.useSsl();
        }

        LettuceConnectionFactory factory = new LettuceConnectionFactory(
            redisConfig, 
            builder.build()
        );
        
        // 커넥션 공유 설정 (true: 같은 커넥션 공유, false: 매번 새로운 커넥션)
        factory.setShareNativeConnection(true);
        
        return factory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        
        // key와 value의 직렬화/역직렬화에 StringRedisSerializer 사용
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        
        // Hash operation에서 사용할 직렬화/역직렬화 설정
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        
        // 기본 직렬화/역직렬화 설정
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        
        redisTemplate.setEnableDefaultSerializer(true);
        redisTemplate.afterPropertiesSet();
        
        return redisTemplate;
    }
}

