package com.shortenurl.user.service;

import com.shortenurl.exception.BadRequestException;
import com.shortenurl.exception.InternalServerException;
import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;

import io.netty.handler.codec.http.HttpHeaderValues;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2UserService {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoOAuthClientId ;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoOAuthRedirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String kakaoAuthorizationUri;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoOAuthUserInfoUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoOAuthTokenUri;

    public String getKakaoOAuthRedirectUri() {
        return kakaoOAuthRedirectUri + "?client_id=" + kakaoOAuthClientId + "&redirect_uri=" + kakaoOAuthRedirectUri + "&response_type=code";
    }

    public String grantKakaoAuthorizationCode() {
        try {
            return WebClient.create(kakaoAuthorizationUri).get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("client_id", kakaoOAuthClientId)
                            .queryParam("redirect_uri", kakaoOAuthRedirectUri)
                            .queryParam("response_type", "code")
                            .build(true)
                    )
                    .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new BadRequestException("Invalid Parameter")))
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new InternalServerException("Fail to login with Kakao account")))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to redirect kakao oauth2 login, response={}, error={}", e.getMessage());
            throw new InternalServerException("Failed to kakao oauth2 login");
        }
    }

    @Transactional
    public User createKakaoUser(String kakaoAccessToken, String providerId) {
        Map<String, Object> userInfo = getKakaoUserInfo(kakaoAccessToken);

        Map<String, Object> kakaoUser = (Map<String, Object>) userInfo.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoUser.get("profile");

        String email = String.valueOf(kakaoUser.get("email"));
        String name = String.valueOf(profile.get("nickname"));
        String id = String.valueOf(kakaoUser.get("id"));
        log.debug("[[test]] id: " + id + ", email: " + email + ", name: " + name + " ");

        User user = new User(email, UserState.NORMAL, name, UserProvider.KAKAO, providerId);
        log.info("OAuth user created: {}", user);

        return user;
    }

    public String requestKakaoAccessToken(String code) {
        try {
            Map<String, Object> response = WebClient.create(kakaoOAuthTokenUri)
                    .post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("client_id", kakaoOAuthClientId)
                            .with("redirect_uri", kakaoOAuthRedirectUri)
                            .with("code", code)
                    )
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> 
                        Mono.error(new BadRequestException("Invalid authorization code or client credentials")))
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> 
                        Mono.error(new InternalServerException("Kakao OAuth server error")))
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("access_token")) {
                log.error("Invalid response from Kakao OAuth server: {}", response);
                throw new InternalServerException("Failed to get access token from Kakao");
            }

            return String.valueOf(response.get("access_token"));
        } catch (BadRequestException | InternalServerException e) {
            log.error("Failed to request Kakao access token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while requesting Kakao access token: {}", e.getMessage());
            throw new InternalServerException("Failed to process Kakao OAuth request");
        }
    }

    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(kakaoOAuthUserInfoUri + "?access_token=" + accessToken, Map.class);
        log.debug("Kakao user info: {}", response);
        return response;
    }
}
