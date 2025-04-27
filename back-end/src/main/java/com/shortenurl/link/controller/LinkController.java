package com.shortenurl.link.controller;

import com.shortenurl.link.domain.Link;
import com.shortenurl.link.dto.CreateLinkRequest;
import com.shortenurl.link.dto.FreeLinkResponse;
import com.shortenurl.link.service.LinkService;
import com.shortenurl.util.ClientMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/link")
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;

    @PostMapping("/free")
    public ResponseEntity<FreeLinkResponse> createFreeLink(
            @Valid @RequestBody CreateLinkRequest requestDto,
            HttpServletRequest httpRequest) {
        String clientIp = ClientMapper.parseClientIp(httpRequest);
        Link link = linkService.createFreeLink(requestDto.getOriginalUrl(), clientIp);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new FreeLinkResponse(link));
    }
}
