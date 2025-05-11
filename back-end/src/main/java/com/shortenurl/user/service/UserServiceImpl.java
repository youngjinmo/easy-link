package com.shortenurl.user.service;

import com.shortenurl.cache.dto.SessionValue;
import com.shortenurl.cache.service.CacheService;
import com.shortenurl.exception.UserNotFoundException;
import com.shortenurl.link.service.LinkService;
import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;
import com.shortenurl.user.dto.OAuthLoginDto;
import com.shortenurl.user.repository.UserRepository;
import com.shortenurl.util.ClientMapper;

import jakarta.servlet.http.HttpServletRequest;
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

    @Transactional
    public String handleKakaoLogin(HttpServletRequest request, String code) {
        // 1. 클라이언트 정보 파싱
        String clientIp = ClientMapper.parseClientIp(request);
        String clientDevice = ClientMapper.parseClientDevice(request);
        OAuthLoginDto oAuthLoginDto = new OAuthLoginDto(code, clientIp, clientDevice);

        // 1. 인가 코드로 액세스 토큰 요청
        String kakaoAccessToken = oAuth2UserService.requestKakaoAccessToken(oAuthLoginDto.getCode());

        // 2. 카카오 사용자 정보 조회
        Map<String, Object> kakaoUserInfo = oAuth2UserService.getKakaoUserInfo(kakaoAccessToken);

        // 3. 사용자 생성 또는 로그인
        String providerId = String.valueOf(kakaoUserInfo.get("id"));
        User user = userRepository.findByProviderAndProviderId(UserProvider.KAKAO, providerId)
                .orElseGet(() -> createKakaoUser(kakaoAccessToken, providerId));

        // 4. 세션 생성 및 토큰 발급
        SessionValue session = SessionValue.builder()
                .userId(user.getId())
                .clientIp(oAuthLoginDto.getClientIp())
                .clientDevice(oAuthLoginDto.getClientDevice())
                .build();
        String accessToken = tokenService.createAccessToken(session);
        cacheService.setLoginSession(session, accessToken);

        // 6. 마지막 로그인 시간 업데이트
        user.setLastLoginAt(LocalDateTime.now());

        return accessToken;
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

    @Transactional
    public User createKakaoUser(String kakaoToken, String providerId) {
        User user = oAuth2UserService.createKakaoUser(kakaoToken, providerId);
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
    public void deleteUser(String accessToken) {
        Long userId = tokenService.decodeAccessToken(accessToken);
        User user = findById(userId);
        // TODO hard delete link log
        // TODO soft delete link and make user into null - why soft delete? cuz error handling
        userRepository.delete(user);
        log.info("User deleted = {}", user.getId());
    }
}
