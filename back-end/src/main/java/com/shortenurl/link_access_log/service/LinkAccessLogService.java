package com.shortenurl.link_access_log.service;

import com.shortenurl.stream.constant.AccessLinkLogPolicy;
import com.shortenurl.stream.dto.AccessLinkLogDto;
import com.shortenurl.stream.service.StreamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkAccessLogService {
    private final StreamService streamService;

    public void putRequestInfo(Long linkId, AccessLinkLogDto requestInfo) {
        try {
            String key = generateKey(linkId);
            Map<String, String> message = convertRequestInfoToMap(requestInfo);
            streamService.produce(key, message);
        } catch (Exception e) {
            log.error("failed to save access log to redis streams, linkId={}, requestInfo={}", linkId, requestInfo.toString());
        }
    }

    public List<Map<String, String>> getRequestInfo(Long linkId) {
        try {
            String key = generateKey(linkId);
            return streamService.consume(key);
        } catch (Exception e) {
            log.error("failed to read access log from redis streams, linkId={}", linkId);
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, String>> getRequestInfo(Long linkId, int limit) {
        try {
            String key = generateKey(linkId);
            return streamService.consume(key, limit);
        } catch (Exception e) {
            log.error("failed to read access log from redis streams, linkId={}", linkId);
            throw new RuntimeException(e);
        }
    }

    private String generateKey(Long linkId) {
        return AccessLinkLogPolicy.STREAM_KEY_PREFIX + ":" + linkId;
    }

    private Map<String, String> convertRequestInfoToMap(AccessLinkLogDto requestInfo) {
        Map<String, String> message = new ConcurrentHashMap<>();
            message.put("clientIp", requestInfo.getClientIp());
            message.put("clientDevice", requestInfo.getClientDevice());
            message.put("clientBrowser", requestInfo.getClientBrowser());
            message.put("clientOs", requestInfo.getClientOs());
            message.put("countryCode", requestInfo.getCountryCode());
            message.put("regionName", requestInfo.getRegionName());
            message.put("referer", requestInfo.getReferer());
            message.put("createdAt", requestInfo.getCreatedAt().toString());
        return message;
    }
}
