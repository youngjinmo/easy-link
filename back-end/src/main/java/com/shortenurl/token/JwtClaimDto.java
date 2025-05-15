package com.shortenurl.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtClaimDto {
    private Long userId;
    private String clientIp;
    private String clientDevice;
}
