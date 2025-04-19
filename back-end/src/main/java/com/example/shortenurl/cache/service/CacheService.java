package com.example.shortenurl.cache.service;

import com.example.shortenurl.cache.dto.SessionValue;

public interface CacheService {
    // base cache service
    void set(String key, String value, long ttl); // string 형태로 저장
    String getByKey(String key);
    boolean exists(String key);
    void deleteByKey(String key);

    // extended cache service
    void setFreeLink(String clientIp, String userAgent);
    boolean hasFreeLink(String clientIp);
    void setVerificationCode(String email, String code);
    boolean verifyEmailCode(String email, String code);
    void setLoginSession(SessionValue sessionValue);
    boolean verifyLoginSession(String ClientIp, String userAgent);
}
