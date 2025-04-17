package com.example.shortenurl.util;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    public String getIpAddress(HttpServletRequest request) {
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

    public String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent.isBlank() ? "unknown": userAgent;
    }
}

