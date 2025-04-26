package com.shortenurl.stream.dto;

import com.shortenurl.link_access_log.dto.GeoInfoDto;
import com.shortenurl.link_access_log.dto.UserAgentDto;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class AccessLinkLogDto {
    private String clientIp;
    private String clientDevice;
    private String clientBrowser;
    private String clientOs;
    private String countryCode;
    private String regionName;
    private String referer;
    private LocalDateTime createdAt;

    private AccessLinkLogDto() {}

    public AccessLinkLogDto(String clientIp, String clientDevice, String clientBrowser, String clientOs, String countryCode, String regionName, String referer) {
        this.clientIp = clientIp;
        this.clientDevice = clientDevice;
        this.clientBrowser = clientBrowser;
        this.clientOs = clientOs;
        this.countryCode = countryCode;
        this.regionName = regionName;
        this.referer = referer;
        this.createdAt = LocalDateTime.now();
    }

    public static AccessLinkLogDto from(String clientIp, String referer, UserAgentDto userAgentDto, GeoInfoDto geoInfoDto) {
        return new AccessLinkLogDto(
                clientIp,
                userAgentDto.getClientDevice(),
                userAgentDto.getClientBrowser(),
                userAgentDto.getClientOs(),
                geoInfoDto.getCountryCode(),
                geoInfoDto.getRegionName(),
                referer
                );
    }
}
