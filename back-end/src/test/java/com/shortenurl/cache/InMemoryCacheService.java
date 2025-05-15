package com.shortenurl.cache;

import com.shortenurl.cache.service.CacheService;
import com.shortenurl.token.JwtClaimDto;

import java.util.Map;

import static com.shortenurl.cache.constant.CacheConstant.*;

public class InMemoryCacheService implements CacheService {
    private final Map<String, String> cacheStore;
    private final Long commonTTL;

    public InMemoryCacheService(Map<String, String> mockRedisTemplate) {
        cacheStore = mockRedisTemplate;
        commonTTL = 10L;
    }

    public void set(String key, String value, long ttl) {
        cacheStore.put(key, value);
    }

    public String getByKey(String key) {
        return cacheStore.get(key);
    }

    public boolean exists(String key) {
        return cacheStore.containsKey(key);
    }

    public void deleteByKey(String key) {
        cacheStore.remove(key);
    }

    public void setFreeLink(String clientIp, Long linkId) {
        String key = generateFreeLinkKey(clientIp);
        set(key, String.valueOf(linkId), commonTTL);
    }

    public boolean hasFreeLink(String clientIp) {
        String key = generateFreeLinkKey(clientIp);
        return exists(key);
    }

    public void setVerificationCode(String email, String code) {
        String key = generateEmailVerificationCodeKey(email);
        set(key, code, commonTTL);
    }

    public boolean verifyEmailCode(String email, String code) {
        String key = generateEmailVerificationCodeKey(email);
        String storedCode = getByKey(key);
        return storedCode != null && storedCode.equals(code);
    }

    public void setLoginSession(JwtClaimDto dto, String token) {
        String key = generateLoginSessionKey(token);
        String value = String.valueOf(dto.getUserId());
        set(key, value, commonTTL);
    }

    public boolean verifyLoginSession(String token) {
        String key = generateLoginSessionKey(token);
        return getByKey(key) != null;
    }

    @Override
    public boolean existToken(String token) {
        return getByKey(LOGIN_SESSION_KEY_PREFIX + token) != null;
    }

    public void removeLoginSession(String token) {
        String key = generateLoginSessionKey(token);
        deleteByKey(key);
    }

    private String generateFreeLinkKey(String clientIp) {
        return FREE_LINK_KEY_PREFIX + clientIp + ":";
    }

    private String generateEmailVerificationCodeKey(String email) {
        return EMAIL_VERIFICATION_CODE_KEY_PREFIX + email + ":";
    }

    private String generateLoginSessionKey(String token) {
        return LOGIN_SESSION_KEY_PREFIX + token;
    }
}
