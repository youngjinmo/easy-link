package com.shortenurl.user.dto;

import lombok.Getter;

@Getter
public class OAuthLoginDto extends BaseLoginDto {
    private String code;

    public static OAuthLoginDto builder() {
        return new OAuthLoginDto();
    }

    public OAuthLoginDto code(String code) {
        this.code = code;
        return this;
    }

    public OAuthLoginDto clientIp(String clientIp) {
        super.setClientIp(clientIp);
        return this;
    }

    public OAuthLoginDto clientDevice(String clientDevice) {
        super.setClientDevice(clientDevice);
        return this;
    }

    public OAuthLoginDto build() {
        return this;
    }
}
