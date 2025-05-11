package com.shortenurl.user.service;

import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    String handleKakaoLogin(HttpServletRequest request, String code);
    User findById(Long id);
    User findByUsername(String username);
    User updateUsername(Long id, String newUsername);
    User updateName(Long id, String newName);
    User updateState(Long id, UserState state);
    void logout(String accessToken);
    void deleteUser(String accessToken);
}

