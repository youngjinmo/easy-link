package com.shortenurl.link.dto;

import com.shortenurl.link.domain.Link;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FreeLinkResponse {
    private Long id;
    private String originalUrl;
    private String shortPath;
    private int maxAccessHits;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    private FreeLinkResponse() {}

    public FreeLinkResponse(Link link) {
        this.id = link.getId();
        this.originalUrl = link.getOriginalUrl();
        this.shortPath = link.getShortPath();
        this.maxAccessHits = link.getMaxHits();
        this.createdAt = link.getCreatedAt();
        this.expiredAt = link.getExpiredAt();
    }
}

