package com.shortenurl.user.service;

import com.shortenurl.cache.dto.SessionValue;
import com.shortenurl.exception.InternalServerException;
import com.shortenurl.util.EncodeUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenService {
    private final EncodeUtil encodeUtil;
    private final String salt;

    public TokenService(EncodeUtil encodeUtil) {
        this.encodeUtil = encodeUtil;
        this.salt = "authentication-access-token-encode-salt";
    }

    public String createAccessToken(SessionValue session) {
        return encodeUtil.encode(String.valueOf(session.getUserId()), salt);
    }

    public Long decodeAccessToken(String accessToken) {
        String decoded = encodeUtil.decode(accessToken);
        try {
            String parsed = decoded.substring(salt.length());
            return Long.parseLong(parsed);
        } catch (Exception e) {
            log.error("failed to decode access token: {}, decoded: {}, error: {}", accessToken, decoded, e.getMessage());
            throw new InternalServerException("Wrong access token");
        }
    }
}
