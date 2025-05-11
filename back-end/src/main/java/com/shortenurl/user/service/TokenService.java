package com.shortenurl.user.service;

import com.shortenurl.cache.dto.SessionValue;
import com.shortenurl.util.EncodeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final EncodeUtil encodeUtil;

    public String createAccessToken(SessionValue session) {
        return encodeUtil.encode(String.valueOf(session.getUserId()));
    }
}
