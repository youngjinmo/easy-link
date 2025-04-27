package com.shortenurl.stream.service;

import java.util.List;
import java.util.Map;

public interface StreamConsumer {
    void createGroup(String key, String groupName);
    List<Map<String, String>> consume(String key, String groupName, String consumerName);
    void acknowledge(String key, String groupName, String recordId);
    void remove(String key, String... recordIds);
}
