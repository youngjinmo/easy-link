package com.shortenurl.cache.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SessionValue {
    private Long userId;
    private String clientIp;
    private String userAgent;
}
