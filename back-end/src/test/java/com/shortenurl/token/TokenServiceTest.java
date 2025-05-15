package com.shortenurl.token;

import com.shortenurl.cache.service.CacheService;
import com.shortenurl.exception.TokenExpiredException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock JwtService jwtService;
    @Mock CacheService cacheService;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        long ttl = 600L; // 10 minute
        tokenService = new TokenService(jwtService, cacheService, ttl);
    }

    @Test
    void createToken() {
        // given
        JwtClaimDto dto = JwtClaimDto.builder()
                .userId(1L)
                .clientIp("127.0.0.1")
                .clientDevice("iOS")
                .build();
        String expected = "access-token";
        given(jwtService.generateToken(any(), anyLong())).willReturn(expected);

        // when
        String actual = tokenService.createToken(dto);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void validateToken() {
        // given
        String accessToken = "access-token";
        given(cacheService.existToken(anyString())).willReturn(true);
        given(jwtService.getExpirationTime(anyString())).willReturn(Date.from(Instant.now().plusSeconds(1000L)));

        // when
        boolean result = tokenService.validateToken(accessToken);

        // then
        assertTrue(result);
    }

    @Test
    void parseToken() {
        // given
        JwtClaimDto expected = JwtClaimDto.builder().userId(1L).build();
        given(jwtService.getExpirationTime(anyString())).willReturn(Date.from(Instant.now().plusSeconds(10000L)));
        given(jwtService.parseClaimsDto(anyString())).willReturn(expected);

        // when
        JwtClaimDto actual = tokenService.parseToken("access-token");

        // then
        assertEquals(expected, actual);
    }

    @Test
    void parseToken_withException() {
        // given
        given(jwtService.getExpirationTime(anyString())).willReturn(Date.from(Instant.now().minusSeconds(10000L)));

        // when & then
        assertThrows(TokenExpiredException.class, () -> tokenService.parseToken("access-token"));
    }
}