package com.shortenurl.user.controller;

import com.shortenurl.user.dto.OAuthLoginDto;
import com.shortenurl.user.service.UserService;
import com.shortenurl.util.ClientMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserService userService;

    @PostMapping("/login/oauth/kakao")
    public void kakaoOauth2Login(HttpServletResponse response) {
        userService.redirectKakaoOauth2Login(response);
    }

    @GetMapping("/oauth/kakao/callback")
    public ResponseEntity<?> kakaoOauth2Callback(@RequestParam("code") String code, HttpServletRequest request) {
        String clientIp = ClientMapper.parseClientIp(request);
        String clientDevice = ClientMapper.parseClientDevice(request);

        String response = userService.createOrLoginKakaoUser(
                OAuthLoginDto.builder()
                        .code(code)
                        .clientIp(clientIp)
                        .clientDevice(clientDevice)
                        .build());
        return ResponseEntity.ok(response);
    }

    // logout api
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
        userService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }
}

