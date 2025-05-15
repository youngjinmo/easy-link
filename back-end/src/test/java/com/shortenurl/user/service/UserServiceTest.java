package com.shortenurl.user.service;

import com.shortenurl.cache.service.CacheService;
import com.shortenurl.exception.InternalServerException;
import com.shortenurl.exception.UserNotFoundException;
import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;
import com.shortenurl.user.dto.OAuthLoginDto;
import com.shortenurl.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private Oauth2UserService oAuth2UserService;
    @Mock private CacheService cacheService;
    @Mock private TokenService tokenService;
    @Mock private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(oAuth2UserService, cacheService, tokenService, userRepository);
    }

    @Test
    void handleKakaoLogin() {
        // given
        User user = new User();
        user.setId(1L);

        String code = "kakao-auth-code";
        String token = "kakao-token";
        Long providerId = 12345L;

        given(oAuth2UserService.requestKakaoAccessToken(code)).willReturn(token);
        given(oAuth2UserService.getKakaoUserInfo(token)).willReturn(Map.of("id", providerId));
        given(userRepository.findByProviderAndProviderId(any(), anyString())).willReturn(Optional.of(user));
        given(tokenService.createAccessToken(any())).willReturn("access-token");
        willDoNothing().given(cacheService).setLoginSession(any(), anyString());

        OAuthLoginDto dto = OAuthLoginDto.builder()
                .clientIp("127.0.0.1")
                .clientDevice("Mac")
                .code(code);

        // when
        String accessToken = userService.handleKakaoLogin(dto);

        // then
        assertEquals("access-token", accessToken);
    }

    @Test
    void createKakaoUser() {
        // given
        String kakaoToken = "kakao-access-token";
        String providerId = "12345";

        User mockUser = new User();
        mockUser.setId(1L);

        given(oAuth2UserService.createKakaoUser(kakaoToken, providerId)).willReturn(mockUser);
        given(userRepository.save(mockUser)).willReturn(mockUser);

        // when
        User user = userService.createKakaoUser(kakaoToken, providerId);

        // then
        assertEquals(mockUser.getId(), user.getId());
    }

    @Test
    void logout() {
        // given
        String token = "access-token";
        willDoNothing().given(cacheService).removeLoginSession(token);

        // when & then
        assertDoesNotThrow(() -> userService.logout(token));
    }

    @Test
    void logout_withoutException() {
        // given
        String accessToken = "access-token";
        willThrow(new InternalServerException()).given(cacheService).removeLoginSession(accessToken);

        // when & then
        assertDoesNotThrow(() -> userService.logout(accessToken));
    }

    @Test
    void findById() {
        // given
        User user = new User(); user.setId(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        User result = userService.findById(1L);

        // then
        assertEquals(user, result);
    }

    @Test
    void findById_withException() {
        // given
        given(userRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void findByUsername() {
        // given
        String username = "test@gmail.com";
        User user = new User(); user.setUsername(username);
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        // when
        User result = userService.findByUsername(username);

        // then
        assertEquals(user, result);
    }

    @Test
    void findByUsername_exception() {
        // given
        String username = "test@gmail.com";
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.findByUsername(username));
    }

    @Test
    void updateUsername() {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("old@gmail.com");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        User result = userService.updateUsername(1L, "new@gmail.com");

        // then
        assertEquals("new@gmail.com", result.getUsername());
    }

    @Test
    void updateUsername_NotFoundException() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.updateUsername(1L, "new"));
    }

    @Test
    void updateName() {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("old");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        User result = userService.updateName(1L, "new");

        // then
        assertEquals("new", result.getName());
    }

    @Test
    void updateState() {
        // given
        User user = new User();
        user.setId(1L);
        user.setState(UserState.NORMAL);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        User result = userService.updateState(1L, UserState.DELETED);

        // then
        assertEquals(UserState.DELETED, result.getState());
    }

    @Test
    void deleteUser() {
        // given
        String accessToken = "access-token";
        given(tokenService.decodeAccessToken(anyString())).willReturn(1L);
        willDoNothing().given(userRepository).deleteById(1L);

        // when
        userService.deleteUser(accessToken);

        // then: 예외 없이 실행되면 성공
        then(userRepository).should().deleteById(1L);
    }
}