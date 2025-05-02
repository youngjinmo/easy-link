package com.shortenurl.user.dto;

import lombok.Getter;

@Getter
public class BaseLoginDto {
    private final String clientIp;
    private final String clientDevice;

    protected BaseLoginDto(String clientIp, String clientDevice) {
        this.clientIp = clientIp;
        this.clientDevice = clientDevice;
    }
}
