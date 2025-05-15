package com.shortenurl.token;

import com.shortenurl.cache.service.CacheService;
import com.shortenurl.exception.TokenExpiredException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
public class TokenService {
    private final long expirationTime; // an hour
    private final JwtService jwtService;
    private final CacheService cacheService;

    public TokenService(
            JwtService jwtService,
            CacheService cacheService,
            @Value("${app.ttl.session}")
            long expirationTime
            ) {
        this.jwtService = jwtService;
        this.cacheService = cacheService;
        this.expirationTime = expirationTime;
    }

    public String createToken(JwtClaimDto dto) {
        return jwtService.generateToken(dto, expirationTime);
    }

    public boolean validateToken(String accessToken) {
        try {
            return cacheService.existToken(accessToken) && !isTokenExpired(accessToken);
        } catch (Exception e) {
            return false;
        }
    }

    public JwtClaimDto parseToken(String accessToken) {
        if (isTokenExpired(accessToken)) {
            throw new TokenExpiredException();
        }

        return jwtService.parseClaimsDto(accessToken);
    }

    private boolean isTokenExpired(String token) {
        try {
            return jwtService
                    .getExpirationTime(token)
                    .before(Date.from(Instant.now()));
        } catch (TokenExpiredException e) {
            return true;
        }
    }
}
