package com.shortenurl.stream.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisStreamService implements StreamService {
    private final StreamOperations<String, Object, Object> redisOperations;

    public RedisStreamService(StringRedisTemplate redisTemplate) {
        this.redisOperations = redisTemplate.opsForStream();
    }

    @Override
    public void produce(String key, Map<String, String> message) {
        try {
            redisOperations.add(key, message);
        } catch (Exception e) {
            log.error("failed to save message to redis streams, key={}", key);
        }
    }

    @Override
    public List<Map<String, String>> consume(String key) {
        // read records from redis streams
        List<MapRecord<String, Object, Object>> records = redisOperations.read(StreamOffset.fromStart(key));

        // remove records loaded
        deleteRecords(key, records);

        return records.stream().map(this::convertRecordToMap).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, String>> consume(String key, int limit) {
        // read records from redis streams
        List<MapRecord<String, Object, Object>> records = redisOperations.read(StreamOffset.fromStart(key));

        // remove records loaded
        deleteRecords(key, records);

        return records.stream().limit(limit).map(this::convertRecordToMap).collect(Collectors.toList());
    }

    private void deleteRecords(String streamKey, List<MapRecord<String, Object, Object>> records) {
        try {
            // remove records loaded
            if (records != null && !records.isEmpty()) {
                redisOperations.delete(streamKey, records.stream()
                        .map(record -> record.getId())
                        .toArray(RecordId[]::new)
                );
                log.info("success to save records from redis streams, count={}", records.size());
            }
        } catch (Exception e) {
            log.error("failed to delete records from redis streams");
        }
    }

    private Map<String, String> convertRecordToMap(MapRecord<String, Object, Object> record) {
        return record.getValue()
                .entrySet()
                .stream()
                .filter(e -> e.getKey() instanceof String
                        && e.getValue() instanceof String)
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().toString()
                ));
    }
}
