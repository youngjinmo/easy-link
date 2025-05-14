package com.shortenurl.user.service;

import com.shortenurl.cache.dto.SessionValue;

import com.shortenurl.util.EncodeUtil;
import com.shortenurl.util.EncoderUtilImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        EncodeUtil encodeUtil = new EncoderUtilImpl();
        tokenService = new TokenService(encodeUtil);
    }

    @Test
    void createAccessToken() {
        // given
        Long userId = 1L;
        String clientIp = "127.0.0.1";
        String clientDevice = "Blackberry";
        SessionValue session = SessionValue.builder()
                .userId(userId)
                .clientIp(clientIp)
                .clientDevice(clientDevice)
                .build();

        // when
        String accessToken = tokenService.createAccessToken(session);

        // then
        assertNotNull(accessToken);
        assertNotEquals(String.valueOf(userId), accessToken);
    }

    @Test
    void decodeAccessToken() {
        // given
        Long userId = 1L;
        String clientIp = "127.0.0.1";
        String clientDevice = "Blackberry";
        SessionValue session = SessionValue.builder()
                .userId(userId)
                .clientIp(clientIp)
                .clientDevice(clientDevice)
                .build();
        String accessToken = tokenService.createAccessToken(session);

        // when
        Long id = tokenService.decodeAccessToken(accessToken);

        // then
        assertEquals(userId, id);
    }
}