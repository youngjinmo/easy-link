package com.shortenurl.stream.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Iterator;
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
        try {
            return redisOperations
                    .read(StreamOffset.fromStart(key))
                    .stream()
                    .map(this::convertRecordToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("failed to read records from redis streams, key={}", key);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, String>> consume(String key, int limit) {
        try {
            return redisOperations
                    .read(StreamOffset.fromStart(key))
                    .stream()
                    .limit(limit)
                    .map(this::convertRecordToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("failed to read records from redis streams, key={}", key);
            throw new RuntimeException(e);
        }
    }

    public void remove(String key, List<Map<String, String>> records) {
        try {
            Iterator<Map<String, String>> iterator = records.iterator();
            while (iterator.hasNext()) {
                Map<String, String> record = iterator.next();
                redisOperations.delete(key, record.get("id"));
            }
        } catch (Exception e) {
            log.info("failed to delete records from redis streams, key={}, count={}", key, records.size());
        }
    }

//    public void remove(String streamKey, List<MapRecord<String, Object, Object>> records) {
//        try {
//            // remove records loaded
//            if (records != null && !records.isEmpty()) {
//                redisOperations.delete(streamKey, records.stream(
//                        .map(record -> record.getId())
//                        .toArray(RecordId[]::new)
//                );
//                log.info("success to save records from redis streams, count={}", records.size());
//            }
//        } catch (Exception e) {
//            log.error("failed to delete records from redis streams");
//        }
//    }

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
