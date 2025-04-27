package com.shortenurl.user.service;

import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;

public interface UserService {
    User createUser(String username, String name);
    User createOAuthUser(String email, String name, UserProvider provider, String providerId);
    User findById(Long id);
    User findByUsername(String username);
    User findOrCreateOAuthUser(String email, String name, UserProvider provider, String providerId);
    User updateUsername(Long id, String newUsername);
    User updateName(Long id, String newName);
    User updateState(Long id, UserState state);
    void deleteUser(Long id);
}
