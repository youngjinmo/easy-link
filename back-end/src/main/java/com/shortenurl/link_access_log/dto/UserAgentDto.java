package com.shortenurl.link_access_log.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class UserAgentDto {
    private String clientDevice;
    private String clientBrowser;
    private String clientOs;
}
