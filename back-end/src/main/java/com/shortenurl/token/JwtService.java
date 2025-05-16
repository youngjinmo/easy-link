package com.shortenurl.token;

import com.shortenurl.exception.TokenExpiredException;
import com.shortenurl.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    // 현재 구조는 secretKey가 Tomcat 메모리에 저장되고 있어, 재부팅시 서버에서 발행한 모든 토큰이 무력화되는 버그 있음.
    // TODO 토큰 발급 시점에 secretKey를 salt로 db에 저장할 필요 있음.
    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    public String generateToken(JwtClaimDto dto, long expirationTime) {
        Date ttl = Date.from(Instant.now().plusSeconds(expirationTime));
        return Jwts.builder()
                .subject(dto.getUserId().toString())
                .claim("clientIp", dto.getClientIp())
                .claim("clientDevice", dto.getClientDevice())
                .expiration(ttl)
                .signWith(secretKey)
                .compact();
    }

    public JwtClaimDto parseClaimsDto(String token) {
        try {
            Claims claims = parseClaims(token);
            return JwtClaimDto.builder()
                    .userId(Long.parseLong(claims.getSubject()))
                    .clientIp(claims.get("clientIp", String.class))
                    .clientDevice(claims.get("clientDevice", String.class))
                    .build();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        }catch (JwtException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    public Date getExpirationTime(String token) {
        return parseClaims(token).getExpiration();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
