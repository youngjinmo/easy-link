package com.shortenurl.cache.service;

import com.shortenurl.token.JwtClaimDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.shortenurl.cache.constant.CacheConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final RedisService baseCacheService;

    @Value("${app.ttl.session}")
    private long loginSessionTTL;

    @Value("${app.ttl.free-link}")
    private long freeLinkTTL;

    @Value("${app.ttl.email-verification}")
    private long emailVerificationTTL;

    // 로그인 하지않고 생성가능한 free link
    public void setFreeLink(String clientIp, Long linkId) {
        String key = generateFreeLinkKey(clientIp);
        set(key, String.valueOf(linkId), freeLinkTTL);
    }

    // free link 생성 여부
    public boolean hasFreeLink(String clientIp) {
        String key = generateFreeLinkKey(clientIp);
        return exists(key);
    }

    // 이메일 인증 코드 저장
    public void setVerificationCode(String email, String code) {
        String key = generateEmailVerificationCodeKey(email);
        set(key, code, emailVerificationTTL);
    }

    // 이메일 인증 코드 비교
    public boolean verifyEmailCode(String email, String code) {
        String key = generateEmailVerificationCodeKey(email);
        String storedCode = getByKey(key);
        return storedCode != null && storedCode.equals(code);
    }

    // 인증 세션 추가
    public void setLoginSession(JwtClaimDto dto, String token) {
        String key = generateLoginSessionKey(token);
        String value = String.valueOf(dto.getUserId());
        set(key, value, loginSessionTTL);
    }

    // ip, useragent 정보로 생성된 로그인 세션이 있는지 여부
    public boolean verifyLoginSession(String token) {
        String key = generateLoginSessionKey(token);
        return getByKey(key) != null;
    }

    public void removeLoginSession(String token) {
        String key = generateLoginSessionKey(token);
        deleteByKey(key);
    }

    // 로그인 하지않고 ip + user-agent 기반으로 무료 링크 생성한지 여부 key
    private String generateFreeLinkKey(String clientIp) {
        return FREE_LINK_KEY_PREFIX + clientIp;
    }

    // 이메일 유효성 검증을 위한 인증코드 key
    private String generateEmailVerificationCodeKey(String email) {
        return EMAIL_VERIFICATION_CODE_KEY_PREFIX + email;
    }

    // 로그인 세션 key
    private String generateLoginSessionKey(String token) {
        return LOGIN_SESSION_KEY_PREFIX + token;
    }

    // 서버(cache)에 존재하는 캐시인지 유효성
    public boolean existToken(String token) {
        return baseCacheService.exists(generateLoginSessionKey(token));
    }

    /**
     * Cache에 해당하는 영역을 다른 서비스로 쉽게 교체하기 위함과
     * 동시에 다른 서비스에서는 CacheService만 의존하기 위함
     */

    public void set(String key, String value, long duration) {
        baseCacheService.set(key, value, duration);
    }

    public String getByKey(String key) {
        return baseCacheService.getByKey(key);
    }

    public boolean exists(String key) {
        return baseCacheService.exists(key);
    }

    public boolean hasExpired(String key) {
        return baseCacheService.hasExpired(key);
    }

    public void deleteByKey(String key) {
        baseCacheService.deleteByKey(key);
    }
}
