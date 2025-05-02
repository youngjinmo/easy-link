package com.shortenurl.user.service;

import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;
import com.shortenurl.user.dto.OAuthLoginDto;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    String createOrLoginKakaoUser(OAuthLoginDto dto);
    User findById(Long id);
    User findByUsername(String username);
    User findOrCreateOAuthUser(UserProvider provider, String providerId, String code);
    User updateUsername(Long id, String newUsername);
    User updateName(Long id, String newName);
    User updateState(Long id, UserState state);
    void logout(String accessToken);
    void deleteUser(Long id);
    void redirectKakaoOauth2Login(HttpServletResponse response);
}

