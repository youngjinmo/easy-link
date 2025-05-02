package com.shortenurl.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthLoginDto extends BaseLoginDto {
    private String code;
    private String clientIp;
    private String clientDevice;

    public OAuthLoginDto(String code, String clientIp, String clientDevice) {
        super(clientIp, clientDevice);
        this.code = code;
    }
}
