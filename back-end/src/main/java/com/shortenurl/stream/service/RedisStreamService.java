package com.shortenurl.stream.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisStreamService implements StreamService {
    private final StringRedisTemplate redisTemplate;

    @Override
    public void produce(String key, Map<String, String> message) {
        redisTemplate.opsForStream().add(key, message);
    }

    @Override
    public List<Map<String, String>> consume(String key) {
        return redisTemplate.opsForStream()
                .read(StreamOffset.fromStart(key))
                .stream()
                .map(this::convertRecordToMap)
                .collect(Collectors.toList());
    }

    private Map<String, String> convertRecordToMap(org.springframework.data.redis.connection.stream.MapRecord<String, Object, Object> record) {
        return record.getValue().entrySet().stream()
                .filter(e -> e.getKey() instanceof String
                        && e.getValue() instanceof String)
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey().toString()),
                        e -> String.valueOf(e.getValue().toString())
                ));
    }
}
