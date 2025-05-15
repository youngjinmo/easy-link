package com.shortenurl.cache.service;

import com.shortenurl.token.JwtClaimDto;

public interface CacheService {
    // base cache service
    void set(String key, String value, long ttl); // string 형태로 저장
    String getByKey(String key);
    boolean exists(String key);
    void deleteByKey(String key);

    // extended cache service
    void setFreeLink(String clientIp, Long linkId);
    boolean hasFreeLink(String clientIp);
    void setVerificationCode(String email, String code);
    boolean verifyEmailCode(String email, String code);
    void setLoginSession(JwtClaimDto dto, String token);
    boolean verifyLoginSession(String token);
    boolean existToken(String token);
    void removeLoginSession(String token);
}
