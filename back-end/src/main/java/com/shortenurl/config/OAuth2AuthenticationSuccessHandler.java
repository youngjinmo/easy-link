package com.shortenurl.config;

import com.shortenurl.user.domain.User;
import com.shortenurl.cache.service.CacheService;
import com.shortenurl.user.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final CacheService cacheService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();
        String provider = token.getAuthorizedClientRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = getEmail(attributes, provider);
        String name = getName(attributes, provider);
        String providerId = getProviderId(attributes, provider);

        User user = userService.findOrCreateUser(email, name, provider, providerId);
//        cacheService.set(request, user);

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:8080");
    }

    private String getEmail(Map<String, Object> attributes, String provider) {
        if ("google".equals(provider)) {
            return (String) attributes.get("email");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("email");
        }
        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }

    private String getName(Map<String, Object> attributes, String provider) {
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return (String) properties.get("nickname");
        }
        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }

    private String getProviderId(Map<String, Object> attributes, String provider) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub");
        } else if ("kakao".equals(provider)) {
            return String.valueOf(attributes.get("id"));
        }
        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }
}

