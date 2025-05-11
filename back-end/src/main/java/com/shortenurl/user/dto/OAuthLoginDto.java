package com.shortenurl.user.dto;

import lombok.Getter;

@Getter
public class OAuthLoginDto extends BaseLoginDto {
    private final String code;

    public OAuthLoginDto(String code, String clientIp, String clientDevice) {
        super(clientIp, clientDevice);
        this.code = code;
    }
}
