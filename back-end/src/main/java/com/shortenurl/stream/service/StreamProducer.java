package com.shortenurl.stream.service;

import java.util.Map;

public interface StreamProducer {
    void produce(String key, Map<String, String> message);
}
