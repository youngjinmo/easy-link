package com.shortenurl.link_access_log.domain;

import com.shortenurl.link.domain.Link;
import com.shortenurl.stream.dto.AccessLinkLogDto;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "link_access_logs")
@Getter
@NoArgsConstructor
public class LinkAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Link link;

    private String clientIp;

    private String clientDevice;

    private String clientBrowser;

    private String clientOs;

    private String countryCode;

    private String regionName;

    private String referer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public LinkAccessLog(
            Link link, String clientIp, String clientDevice, String clientBrowser, String clientOs,
            String countryCode, String regionName, String referer, LocalDateTime createdAt
    ) {
        this.link = link;
        this.clientIp = clientIp;
        this.clientDevice = clientDevice;
        this.clientBrowser = clientBrowser;
        this.clientOs = clientOs;
        this.countryCode = countryCode;
        this.regionName = regionName;
        this.referer = referer;
        this.createdAt = createdAt;
    }

    public LinkAccessLog(Link link, AccessLinkLogDto accessLinkLogDto) {
        this.link = link;
        this.clientIp = accessLinkLogDto.getClientIp();
        this.clientDevice = accessLinkLogDto.getClientDevice();
        this.clientBrowser = accessLinkLogDto.getClientBrowser();
        this.clientOs = accessLinkLogDto.getClientOs();
        this.countryCode = accessLinkLogDto.getCountryCode();
        this.regionName = accessLinkLogDto.getRegionName();
        this.referer = accessLinkLogDto.getReferer();
        this.createdAt = accessLinkLogDto.getCreatedAt();
    }
} 

