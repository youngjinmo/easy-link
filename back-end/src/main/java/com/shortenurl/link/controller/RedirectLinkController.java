package com.shortenurl.link.controller;

import com.shortenurl.link.domain.Link;
import com.shortenurl.link.service.LinkService;
import com.shortenurl.link_access_log.service.LinkAccessLogService;
import com.shortenurl.stream.dto.AccessLinkLogDto;
import com.shortenurl.util.ClientMapper;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/i")
@RequiredArgsConstructor
public class RedirectLinkController {
    private final LinkService linkService;
    private final LinkAccessLogService linkAccessLogService;

    @GetMapping("/{shortPath}")
    public ResponseEntity<Void> redirect(@PathVariable String shortPath, HttpServletRequest request) {
        Link link = linkService.findValidateLinkByPath(shortPath);

        if (link != null) {
            // access log 적재 -> cache storage
            AccessLinkLogDto requestInfo = ClientMapper.parseRequestInfo(request);
            linkAccessLogService.putRequestInfo(link.getId(), requestInfo);

            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create(link.getOriginalUrl()))
                    .build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .location(URI.create("/"))
                .build();
    }
}
