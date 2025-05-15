package com.shortenurl.user.service;

import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;
import com.shortenurl.user.dto.OAuthLoginDto;

public interface UserService {
    String handleKakaoLogin(OAuthLoginDto oAuthLoginDto);
    User createKakaoUser(String kakaoToken, String providerId);
    void logout(String accessToken);
    User findById(Long id);
    User findByUsername(String username);
    User updateUsername(Long id, String newUsername);
    User updateName(Long id, String newName);
    User updateState(Long id, UserState state);
    void deleteUser(String accessToken);
}

