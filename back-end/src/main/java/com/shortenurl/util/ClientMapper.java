package com.shortenurl.util;

import com.shortenurl.link_access_log.dto.GeoInfoDto;
import com.shortenurl.link_access_log.dto.UserAgentDto;
import com.shortenurl.stream.dto.AccessLinkLogDto;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ua_parser.Client;
import ua_parser.Parser;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientMapper {
    private final Parser uaParser;
    private final String UNKNOWN = "unknown";

    public AccessLinkLogDto parseRequestInfo(HttpServletRequest request) {
        String clientIp = parseClientIp(request);
        String referer = parseReferer(request);
        UserAgentDto userAgent = parseUserAgent(request);
        GeoInfoDto geoInfo = parseGeoInfo(clientIp);
        return AccessLinkLogDto.from(clientIp, referer, userAgent, geoInfo);
    }

    public String parseClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getHeader("X-Real-clientIp");
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        // localhost 에서 동작시 clientIpv6, clientIpv4로 변환
        if (clientIp != null && clientIp.equals("0:0:0:0:0:0:0:1")) {
            return "127.0.0.1";
        }

        if (clientIp != null && !clientIp.isEmpty()) {
            clientIp = clientIp.contains(",") ? clientIp.split(",")[0].trim() : clientIp;
        }
        return clientIp;
    }

    public String parseClientDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        Client client = uaParser.parse(userAgent);
        return client.device.family;
    }

    private String parseReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isEmpty()) {
            return UNKNOWN;
        }
        return referer.contains("://") ? referer : "http://" + referer;
    }

    private UserAgentDto parseUserAgent(HttpServletRequest request) {
        String uaString = request.getHeader("User-Agent");
        try {
            Client client = uaParser.parse(uaString);
            return UserAgentDto.builder()
                    .clientDevice(client.device.family)
                    .clientBrowser(client.userAgent.family)
                    .clientOs(client.os.family)
                    .build();
        } catch (Exception e) {
            log.error("failed to parse user-agent, origin user-agent={}", uaString);
            return UserAgentDto.builder()
                    .clientDevice(UNKNOWN)
                    .clientBrowser(UNKNOWN)
                    .clientOs(UNKNOWN)
                    .build();
        }
    }

    private GeoInfoDto parseGeoInfo(String clientIp) {
        try {
            // TODO make it by use external library or api
            String countryCode = UNKNOWN;
            String regionName = UNKNOWN;

            return GeoInfoDto.builder()
                    .countryCode(countryCode)
                    .regionName(regionName)
                    .build();
        } catch (Exception e) {
            log.error("failed to parse geolocation, origin clientIp={}", clientIp);
            return GeoInfoDto.builder()
                    .countryCode(UNKNOWN)
                    .regionName(UNKNOWN)
                    .build();
        }
    }
}

