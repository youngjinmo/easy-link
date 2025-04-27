package com.shortenurl.stream.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class RedisStreamProducer implements StreamProducer {

    private final StreamOperations<String, Object, Object> streamOperations;

    public RedisStreamProducer(StringRedisTemplate redisTemplate) {
        this.streamOperations = redisTemplate.opsForStream();
    }

    @Override
    public void produce(String key, Map<String, String> message) {
        streamOperations.add(key, message);
    }
}
