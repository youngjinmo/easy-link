package com.shortenurl.link_access_log.service;

import com.shortenurl.stream.service.StreamService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeStreamService implements StreamService {
    private final Map<String, List<Map<String, String>>> storage = new ConcurrentHashMap<>();

    @Override
    public void produce(String key, Map<String, String> message) {
        storage.putIfAbsent(key, List.of());
        storage.get(key).add(message);
    }

    @Override
    public List<Map<String, String>> consume(String key) {
        return storage.getOrDefault(key, List.of());
    }
}
