package com.shortenurl.cache.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public void set(String key, String value, long ttl) {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    public String getByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean exists(String key) {
        return redisTemplate.persist(key);
    }

    public boolean hasExpired(String key) {
        // redis 2.6 or older: expired -> -1
        // redis 2.8: expired -> -2, no expire -> -1
        // ref. https://redis.io/docs/latest/commands/ttl/
        return redisTemplate.getExpire(key) == -2;
    }

    public void deleteByKey(String key) {
        redisTemplate.delete(key);
    }
}
