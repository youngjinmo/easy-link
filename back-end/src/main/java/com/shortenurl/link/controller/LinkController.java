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
    private final ClientMapper clientMapper;

    @PostMapping("/free")
    public ResponseEntity<FreeLinkResponse> createFreeLink(
            HttpServletRequest request,
            @Valid @RequestBody CreateLinkRequest requestDto) {
        String clientIp = clientMapper.parseClientIp(request);
        Link link = linkService.createFreeLink(requestDto.getOriginalUrl(), clientIp);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new FreeLinkResponse(link));
    }
}
