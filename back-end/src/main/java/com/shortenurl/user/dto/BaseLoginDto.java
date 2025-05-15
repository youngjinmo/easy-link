package com.shortenurl.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BaseLoginDto {
    private String clientIp;
    private String clientDevice;
}
