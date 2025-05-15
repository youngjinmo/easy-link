package com.shortenurl.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortenurl.exception.InternalServerException;
import com.shortenurl.token.JwtClaimDto;
import com.shortenurl.token.TokenService;
import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;
import com.shortenurl.util.ClientMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final ClientMapper clientMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            OAuth2AuthenticationToken oauth2AuthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String authorizedClient = oauth2AuthToken.getAuthorizedClientRegistrationId();
            User user = switch(authorizedClient.toUpperCase()) {
                case "KAKAO" -> creatFromKakaoAccount(oAuth2User);
                case "GOOGLE" -> createFromGoogleAccount(oAuth2User);
                default -> {
                    log.error("oauth provider name is not supported: {}", oauth2AuthToken.getAuthorizedClientRegistrationId());
                    throw new InternalServerException("oauth provider name is not supported");
                }
            };
            JwtClaimDto jwtDto = JwtClaimDto.builder()
                    .userId(user.getId())
                    .clientIp(clientMapper.parseClientIp(request))
                    .clientDevice(clientMapper.parseClientDevice(request))
                    .build();
            String token = tokenService.createToken(jwtDto);
    
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Map.of("accessToken", token)));
            log.info("success to login with oauth2");
        } catch (Exception e) {
            log.error("OAuth2 authentication success handler error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Authentication processing failed");
        }
    }

    // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
    private User creatFromKakaoAccount(OAuth2User oAuth2User) {
        String username = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("id");
        UserState state = UserState.NORMAL;
        return new User(username, state, name, UserProvider.KAKAO, providerId);
    }

    // https://developers.google.com/identity/account-linking/oauth-linking?hl=ko
    private User createFromGoogleAccount(OAuth2User oAuth2User) {
        String username = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");
        UserState state = UserState.NORMAL;
        return new User(username, state, name, UserProvider.GOOGLE, providerId);
    }
}
