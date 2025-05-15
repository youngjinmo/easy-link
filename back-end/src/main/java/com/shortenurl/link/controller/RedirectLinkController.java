package com.shortenurl.link.controller;

import com.shortenurl.exception.CustomException;
import com.shortenurl.exception.ExceedFreeLinkLimitException;
import com.shortenurl.exception.LinkExpiredException;
import com.shortenurl.exception.LinkNotFoundException;
import com.shortenurl.link.domain.Link;
import com.shortenurl.link.service.LinkService;
import com.shortenurl.link_access_log.service.LinkAccessLogService;
import com.shortenurl.stream.dto.AccessLinkLogDto;
import com.shortenurl.util.ClientMapper;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;

@RestController
@RequestMapping("/i")
@RequiredArgsConstructor
public class RedirectLinkController {
    @Value("${app.frontend.url}")
    private String vueServer;
    private final LinkService linkService;
    private final LinkAccessLogService linkAccessLogService;
    private final ClientMapper clientMapper;

    @GetMapping("/{shortPath}")
    public ResponseEntity<Void> redirect(@PathVariable String shortPath, HttpServletRequest request) {
        Link link = linkService.findValidateLinkByPath(shortPath);

        if (link != null) {
            // access log 적재 -> cache storage
            AccessLinkLogDto requestInfo = clientMapper.parseRequestInfo(request);
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

    @ExceptionHandler({ LinkExpiredException.class, LinkNotFoundException.class, ExceedFreeLinkLimitException.class })
    public RedirectView handleException(CustomException e) {
        String formattedUrl = String.format("%s?errorCode=%d&message=%s", vueServer, e.statusCode(), e.getMessage() );
        return new RedirectView(formattedUrl);
    }
}
