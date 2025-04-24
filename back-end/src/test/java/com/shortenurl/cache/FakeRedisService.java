package com.shortenurl.cache;

import com.shortenurl.cache.dto.SessionValue;
import com.shortenurl.cache.service.CacheService;
import com.shortenurl.util.EncodeUtil;
import com.shortenurl.util.EncoderUtilImpl;

import java.util.Map;

import static com.shortenurl.cache.constant.CacheConstant.*;

public class FakeRedisService implements CacheService {
    private final Map<String, String> cacheStore;
    private final EncodeUtil encoderUtil;
    private final Long commonTTL;

    public FakeRedisService(Map<String, String> mockRedisTemplate) {
        cacheStore = mockRedisTemplate;
        encoderUtil = new EncoderUtilImpl();
        commonTTL = 0L;
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

    public void setFreeLink(String clientIp, String userAgent) {
        String key = generateFreeLinkKey(clientIp);
        set(key, String.valueOf(userAgent), commonTTL);
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

    public void setLoginSession(SessionValue sessionValue) {
        String key = generateLoginSessionKey(sessionValue.getClientIp(), sessionValue.getUserAgent());
        String value = String.valueOf(sessionValue.getUserId());
        set(key, value, commonTTL);
    }

    public boolean verifyLoginSession(String clientIp, String userAgent) {
        String key = generateLoginSessionKey(clientIp, userAgent);
        return getByKey(key) != null;
    }

    private String generateFreeLinkKey(String clientIp) {
        return FREE_LINK_KEY_PREFIX + clientIp + ":";
    }

    private String generateEmailVerificationCodeKey(String email) {
        return EMAIL_VERIFICATION_CODE_KEY_PREFIX + email + ":";
    }

    private String generateLoginSessionKey(String clientIp, String userAgent) {
        return LOGIN_SESSION_KEY_PREFIX + encoderUtil.encode(clientIp + userAgent) + ":";
    }
}
