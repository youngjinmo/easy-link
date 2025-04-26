package com.shortenurl.link_access_log.service;

import com.shortenurl.stream.constant.AccessLinkLogPolicy;
import com.shortenurl.stream.dto.AccessLinkLogDto;
import com.shortenurl.stream.service.StreamService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LinkAccessLogService {
    private final StreamService streamService;

    public void putRequestInfo(Long linkId, AccessLinkLogDto requestInfo) {
        String key = generateKey(linkId);
        Map<String, String> message = convertRequestInfoToMap(requestInfo);
        streamService.produce(key, message);
    }

    public List<Map<String, String>> readRequestInfo(Long linkId) {
        String key = generateKey(linkId);
        return streamService.consume(key);
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
        return message;
    }
}
