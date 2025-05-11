package com.shortenurl.user.controller;

import com.shortenurl.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/oauth/kakao/callback")
    public ResponseEntity<?> kakaoOauth2Login(@RequestParam String code, HttpServletRequest request) {
        String accessToken = userService.handleKakaoLogin(request, code);
        return ResponseEntity.ok().body(accessToken);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
        userService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String accessToken) {
        userService.deleteUser(accessToken);
        return ResponseEntity.noContent().build();
    }
}
