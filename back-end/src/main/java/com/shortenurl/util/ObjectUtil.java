package com.shortenurl.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ObjectUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String stringify(Object resource) {
        try {
            return objectMapper.writeValueAsString(resource);
        } catch (Exception e) {
            log.error("failed to stringify object: {}", resource);
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseJson(String resource, Class<T> clazz) {
        try {
            return objectMapper.readValue(resource, clazz);
        } catch (Exception e) {
            log.error("failed to parse json: {}", resource);
            throw new RuntimeException(e);
        }
    }
}

