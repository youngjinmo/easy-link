package com.shortenurl.link_access_log.service;

import com.shortenurl.stream.service.StreamService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FakeStreamService implements StreamService {
    private final Map<String, List<Map<String, String>>> queue; // new ConcurrentHashMap<>();

    public FakeStreamService(Map<String, List<Map<String, String>>> queue) {
        this.queue = queue;
    }

    @Override
    public void produce(String key, Map<String, String> message) {
        List<Map<String, String>> queue = this.queue.computeIfAbsent(key, k -> new ArrayList<>());
        queue.add(message);
    }

    @Override
    public List<Map<String, String>> consume(String key) {
        List<Map<String, String>> originalMessages = queue.getOrDefault(key, new ArrayList<>());
        List<Map<String, String>> messages = new ArrayList<>(originalMessages);

        // remove messages from queue
        removeElements(key, messages);

        return messages;
    }

    @Override
    public List<Map<String, String>> consume(String key, int limit) {
        List<Map<String, String>> originalMessages = queue.getOrDefault(key, new ArrayList<>())
                .stream()
                .limit(limit)
                .toList();
        List<Map<String, String>> messages = new ArrayList<>(originalMessages);

        // remove messages from queue
        removeElements(key, messages);

        return messages;
    }

    private void removeElements(String key, List<Map<String, String>> messages) {
        List<Map<String, String>> originalQueue = queue.getOrDefault(key, new ArrayList<>());

        // 안전한 방식: Iterator 사용
        originalQueue.removeIf(messages::contains);

        if (originalQueue.isEmpty()) {
            queue.remove(key); // 데이터가 없으면 아예 key 자체를 삭제
        }
        System.out.println("remove elements from queue: " + messages.size());
    }
}
