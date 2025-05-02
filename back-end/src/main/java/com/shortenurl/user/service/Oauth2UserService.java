package com.shortenurl.user.service;

import com.shortenurl.exception.InternalServerException;
import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;
import com.shortenurl.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

    public void redirectKakaoOauth2Login(HttpServletResponse response) {
        try {
            response.sendRedirect(kakaoAuthorizationUri
                    + "?client_id=" + kakaoOAuthClientId
                    + "&redirect_uri=" + kakaoOAuthRedirectUri
                    + "&response_type=code");
        } catch (Exception e) {
            log.error("Failed to redirect kakao oauth2 login, response={}, error={}", response, e.getMessage());
            throw new InternalServerException("Failed to kakao oauth2 login");
        }
    }

//    public String callbackKakaoOauth2Login(String code) {
//        String accessToken = requestKakaoAccessToken(code);
//        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);
//        String providerId = String.valueOf(userInfo.get("id"));
//        String username = String.valueOf(userInfo.get("email"));
//
//        if (userRepository.findByProviderAndProviderId(UserProvider.KAKAO, providerId).isPresent()) {
//            // TODO create access token and return in
//            return "";
//        } else {
//            User user = createKakaoUser(code);
//            // TODO create access token and return in
//            return "";
//        }
//    }

    public User createKakaoUser(String code) {
        String accessToken = requestKakaoAccessToken(code);
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

        Map<String, Object> kakaoUser = (Map<String, Object>) userInfo.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoUser.get("profile");

        String email = String.valueOf(kakaoUser.get("email"));
        String name = String.valueOf(profile.get("nickname"));
        String providerId = String.valueOf(kakaoUser.get("id"));

        User user = new User(email, UserState.NORMAL, name, UserProvider.KAKAO, providerId);
        log.info("OAuth user created: {}", user);

        return user;
    }

    public String requestKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthClientId);
        params.add("redirect_uri", kakaoOAuthRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        Map<String, Object> response = restTemplate.postForObject(kakaoOAuthTokenUri, request, Map.class);

        assert response != null;
        return String.valueOf(response.get("access_token")) ;
    }

    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(kakaoOAuthUserInfoUri + "?access_token=" + accessToken, Map.class);
        log.debug("Kakao user info: {}", response);
        return response;
    }
}
