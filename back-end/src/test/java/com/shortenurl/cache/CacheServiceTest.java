package com.shortenurl.cache;

import com.shortenurl.cache.dto.SessionValue;
import com.shortenurl.cache.service.CacheService;
import com.shortenurl.util.EncodeUtil;
import com.shortenurl.util.EncoderUtilImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.shortenurl.cache.constant.CacheConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class CacheServiceTest {

    private CacheService cacheService;
    private Map<String, String> mockCacheStorage;

    @BeforeEach
    void setUp() {
        mockCacheStorage = new HashMap<>();
        cacheService = new InMemoryCacheService(mockCacheStorage);
    }

    @Test
    void set() {
        // given
        String mockKey = "cache-key:";
        String mockValue = "cache-value";
        long mockTTL = 0L;

        // when
        cacheService.set(mockKey, mockValue, mockTTL);

        // then
        assertEquals(mockValue, mockCacheStorage.get(mockKey));
    }

    @Test
    void getByKey() {
        // given
        String mockKey = "cache-key:";
        String mockValue = "cache-value";
        mockCacheStorage.put(mockKey, mockValue);

        // when
        String cacheValue = cacheService.getByKey(mockKey);

        // then
        assertEquals(mockValue, cacheValue);
    }

    @Test
    void exists() {
        // given
        String mockKey = "cache-key:";
        String mockValue = "cache-value";
        mockCacheStorage.put(mockKey, mockValue);

        // then
        assertTrue(cacheService.exists(mockKey));
        assertFalse(cacheService.exists(mockKey + "2"));
    }

    @Test
    void deleteByKey() {
        // given
        String mockKey = "cache-key:";
        String mockValue = "cache-value";
        mockCacheStorage.put(mockKey, mockValue);

        // when
        cacheService.deleteByKey(mockKey);

        // then
        assertNull(mockCacheStorage.get(mockKey));
    }

    @Test
    void setFreeLink() {
        // given
        long freeLinkId = 0L;
        String clientIp = "localhost";
        String cacheKey = FREE_LINK_KEY_PREFIX + clientIp + ":";

        // when
        cacheService.setFreeLink(clientIp, freeLinkId);

        // then
        assertNotNull(mockCacheStorage.get(cacheKey));
        assertEquals(String.valueOf(freeLinkId), mockCacheStorage.get(cacheKey));
    }

    @Test
    void hasFreeLink() {
        // given
        String clientIp = "localhost";
        String userAgent = "Mozilla";
        String cacheKey = FREE_LINK_KEY_PREFIX + clientIp + ":";
        mockCacheStorage.put(cacheKey, userAgent);

        // then
        assertTrue(cacheService.hasFreeLink(clientIp));
    }

    @Test
    void setVerificationCode() {
        // given
        String email = "test@test.com";
        String code = "email_auth_code";

        // when
        cacheService.setVerificationCode(email, code);

        // then
        String key = EMAIL_VERIFICATION_CODE_KEY_PREFIX + email + ":";
        assertEquals(code, mockCacheStorage.get(key));
    }

    @Test
    void verifyEmailCode() {
        // given
        String email = "test@test.com";
        String code = "email_auth_code";
        String cacheKey = EMAIL_VERIFICATION_CODE_KEY_PREFIX + email + ":";
        mockCacheStorage.put(cacheKey, code);

        // then
        assertTrue(cacheService.verifyEmailCode(email, code));
    }

    @Test
    void setLoginSession() {
        // given
        EncodeUtil encoderUtil = new EncoderUtilImpl();
        Long userId = 1L;
        String clientIp = "localhost";
        String userAgent = "Mozilla";
        SessionValue session = SessionValue.builder()
                .userId(userId)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .build();

        // when
        cacheService.setLoginSession(session);

        // then
        String cacheKey = LOGIN_SESSION_KEY_PREFIX
                + encoderUtil.encode(clientIp + userAgent)
                + ":";
        assertEquals(String.valueOf(userId), mockCacheStorage.get(cacheKey));
    }

    @Test
    void verifyLoginSession() {
        // given
        EncodeUtil encoderUtil = new EncoderUtilImpl();
        Long userId = 1L;
        String clientIp = "localhost";
        String userAgent = "Mozilla";
        SessionValue session = SessionValue.builder()
                .userId(userId)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .build();
        String cacheKey = LOGIN_SESSION_KEY_PREFIX
                + encoderUtil.encode(clientIp + userAgent)
                + ":";
        mockCacheStorage.put(cacheKey, String.valueOf(userId));

        // when
        cacheService.verifyLoginSession(clientIp, userAgent);

        // then
        assertEquals(String.valueOf(userId), mockCacheStorage.get(cacheKey));
    }
}
