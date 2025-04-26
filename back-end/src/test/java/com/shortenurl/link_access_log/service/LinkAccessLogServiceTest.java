package com.shortenurl.link_access_log.service;

import com.shortenurl.stream.constant.AccessLinkLogPolicy;
import com.shortenurl.stream.dto.AccessLinkLogDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class LinkAccessLogServiceTest {

    private LinkAccessLogService accessLogService;
    private Map<String, List<Map<String, String>>> cacheStore;

    @BeforeEach
    void setUp() {
        cacheStore = new ConcurrentHashMap<>();
        accessLogService = new LinkAccessLogService(new FakeStreamService(cacheStore));
    }

    @Test
    @DisplayName("queue에 메시지 저장")
    void putRequestInfo() {
        // given
        Long mockLinkId = 1L;
        int givenQueueLength = 3;

        // when
        for (int i = 0; i < givenQueueLength; i++) {
            accessLogService.putRequestInfo(mockLinkId, generateMockAccessLinkLogDto());
        }

        // then
        String key = AccessLinkLogPolicy.STREAM_KEY_PREFIX + ":" + mockLinkId;
        assertEquals(givenQueueLength, cacheStore.get(key).size());
    }

    @Test
    @DisplayName("queue에서 메시지 가져오기")
    void getRequestInfo() {
        // given
        long mockLinkId = 1L;
        String key = AccessLinkLogPolicy.STREAM_KEY_PREFIX + ":" + mockLinkId;
        int givenQueueLength = 3;
        for (int i = 0; i < givenQueueLength; i++) {
            List<Map<String, String>> queue = cacheStore.computeIfAbsent(key, k -> new ArrayList<>());
            AccessLinkLogDto mockAccessLinkLogDto = generateMockAccessLinkLogDto();
            Map<String, String> message = convertRequestInfoToMap(mockAccessLinkLogDto);
            queue.add(message);
        }

        // when
        List<Map<String, String>> queue = accessLogService.readRequestInfo(mockLinkId);

        // then
        assertEquals(givenQueueLength, queue.size());
        assertEquals(0, cacheStore.getOrDefault(key, new ArrayList<>()).size());
    }

//    @Test
//    @DisplayName("원하는 갯수만큼 메시지 가져오기")

    private Map<String, String> convertRequestInfoToMap(AccessLinkLogDto mockAccessLinkLogDto) {
        Map<String, String> message = new HashMap<>();
        message.put("clientIp", mockAccessLinkLogDto.getClientIp());
        message.put("clientDevice", mockAccessLinkLogDto.getClientDevice());
        message.put("clientBrowser", mockAccessLinkLogDto.getClientBrowser());
        message.put("clientOs", mockAccessLinkLogDto.getClientOs());
        message.put("countryCode", mockAccessLinkLogDto.getCountryCode());
        message.put("regionName", mockAccessLinkLogDto.getRegionName());
        message.put("referer", mockAccessLinkLogDto.getReferer());
        return message;
    }

    private AccessLinkLogDto generateMockAccessLinkLogDto() {
        String mockClientIp = "127.0.0.1";
        String mockClientDevice = "RobotComputer";
        String mockClientBrowser = "Chrome";
        String mockClientOs = "Windows";
        String mockReferer = "https://www.google.com/";
        String mockCountryCode = "US";
        String mockRegionName = "California";
        return new AccessLinkLogDto(mockClientIp, mockClientDevice, mockClientBrowser, mockClientOs, mockCountryCode, mockRegionName, mockReferer);
    }
}