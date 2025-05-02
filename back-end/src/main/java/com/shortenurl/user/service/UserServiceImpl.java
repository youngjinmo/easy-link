package com.shortenurl.user.service;

import com.shortenurl.cache.dto.SessionValue;
import com.shortenurl.cache.service.CacheService;
import com.shortenurl.exception.UserNotFoundException;
import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;
import com.shortenurl.user.dto.OAuthLoginDto;
import com.shortenurl.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Oauth2UserService oAuth2UserService;
    private final CacheService cacheService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public void redirectKakaoOauth2Login(HttpServletResponse response) {
        oAuth2UserService.redirectKakaoOauth2Login(response);
    }

    @Transactional
    public String createOrLoginKakaoUser(OAuthLoginDto oAuthLoginDto) {
        String kakaoAccessToken = oAuth2UserService.requestKakaoAccessToken(oAuthLoginDto.getCode());
        Map<String, Object> kakaoUserInfo = oAuth2UserService.getKakaoUserInfo(kakaoAccessToken);

        String providerId = String.valueOf(kakaoUserInfo.get("id"));
        User user = userRepository.findByProviderAndProviderId(UserProvider.KAKAO, providerId).orElse(null);

        if (user != null) {
            // kakao oauth user login
            SessionValue session = SessionValue.builder()
                    .userId(user.getId())
                    .clientIp(oAuthLoginDto.getClientIp())
                    .clientDevice(oAuthLoginDto.getClientDevice())
                    .build();
            String accessToken = tokenService.createAccessToken(session);
            cacheService.setLoginSession(session, accessToken);
            user.setLastLoginAt(LocalDateTime.now());
            log.info("Kakao user {} logged in", user.getId());
            return accessToken;
        } else {
            // create account
            createKakaoUser(oAuthLoginDto.getCode());

            // login
            SessionValue session = SessionValue.builder()
                    .userId(user.getId())
                    .clientIp(oAuthLoginDto.getClientIp())
                    .clientDevice(oAuthLoginDto.getClientDevice())
                    .build();
            String accessToken = tokenService.createAccessToken(session);
            cacheService.setLoginSession(session, accessToken);
            user.setLastLoginAt(LocalDateTime.now());
            log.info("Kakao user {} logged in", user.getId());
            return accessToken;
        }
    }

    public void logout(String accessToken) {
        try {
            cacheService.removeLoginSession(accessToken);
            log.debug("Logout success, accessToken={}", accessToken);
        } catch (Exception e) {
            log.error("failed to logout, accessToken={}", accessToken);
        }
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    public User findOrCreateOAuthUser(UserProvider provider, String providerId, String code) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> createKakaoUser(code));
    }

    private User createKakaoUser(String code) {
        User user = oAuth2UserService.createKakaoUser(code);
        log.info("OAuth user created: {}, {}", user.getId(), user.getUsername());
        return userRepository.save(user);
    }

    @Transactional
    public User updateUsername(Long id, String newUsername) {
        User user = findById(id);
        user.setUsername(newUsername);
        log.info("User {} username updated to {}", user.getId(), newUsername);
        return user;
    }

    @Transactional
    public User updateName(Long id, String newName) {
        User user = findById(id);
        user.setName(newName);
        log.info("User {} name updated to {}", user.getId(), newName);
        return user;
    }

    @Transactional
    public User updateState(Long id, UserState state) {
        User user = findById(id);
        user.setState(state);
        log.info("User {} state updated to {}", user.getId(), state);
        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        // TODO change into soft delete
        userRepository.delete(user);
        log.info("User deleted = {}", user.getId());
    }
}
