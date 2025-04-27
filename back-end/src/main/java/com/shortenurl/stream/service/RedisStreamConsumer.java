package com.shortenurl.stream.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Redis Stream Consumer
 * write it again in spring batch
 */
@Slf4j
@Service
public class RedisStreamConsumer implements StreamConsumer {

    private final StreamOperations<String, Object, Object> streamOperations;

    public RedisStreamConsumer(StringRedisTemplate redisTemplate) {
        this.streamOperations = redisTemplate.opsForStream();
    }

    @Override
    public void createGroup(String key, String groupName) {
        try {
            streamOperations.createGroup(key, groupName);
        } catch (Exception e) {
            log.warn("failed to create group, key={}, groupName={}", key, groupName);
        }
    }

    @Override
    public List<Map<String, String>> consume(String key, String groupName, String consumerName) {
        try {
            StreamOffset<String> offset = StreamOffset.create(key, ReadOffset.lastConsumed());
            Consumer consumer = Consumer.from(groupName, consumerName);

            List<MapRecord<String, Object, Object>> records = streamOperations.read(
                    consumer,
                    StreamReadOptions.empty().block(Duration.ofSeconds(1L)),
                    offset
            );

            assert records != null;
            return records
                    .stream()
                    .map(record -> record
                            .getValue()
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    e -> e.getKey().toString(),
                                    e -> e.getValue().toString()
                            ))
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("failed to read records from redis streams, key={}, groupName={}, consumerName={}", key, groupName, consumerName);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void acknowledge(String key, String groupName, String recordId) {
        try {
            streamOperations.acknowledge(key, groupName, recordId);
        } catch (Exception e) {
            log.warn("failed to acknowledge, key={}, groupName={}, recordId={}", key, groupName, recordId);
        }
    }

    @Override
    public void remove(String key, String... recordIds) {
        try {
            streamOperations.delete(key, recordIds);
        } catch (Exception e) {
            log.warn("failed to remove records from redis streams, key={}, recordIds={}", key, recordIds);
        }
    }
}
