package com.shortenurl.user.service;

import com.shortenurl.exception.UserNotFoundException;
import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.constant.UserState;
import com.shortenurl.user.domain.User;
import com.shortenurl.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public User createUser(String username, String name) {
        User user = new User(username, name, UserProvider.EMAIL);
        log.info("User created: {}", user);
        return userRepository.save(user);
    }

    public User createOAuthUser(String email, String name, UserProvider provider, String providerId) {
        User user = new User(email, name, provider, providerId);
        log.info("OAuth user created: {}", user);
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    public User findOrCreateOAuthUser(String email, String name, UserProvider provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> createOAuthUser(email, name, provider, providerId));
    }

    @Transactional
    public User updateUsername(Long id, String newUsername) {
        User user = findById(id);
        user.setUsername(newUsername);
        log.info("User {} username updated to {}", user.getId(), newUsername);
        return user;
    }

    @Override
    public User updateName(Long id, String newName) {
        User user = findById(id);
        user.setName(newName);
        log.info("User {} name updated to {}", user.getId(), newName);
        return user;
    }

    @Override
    public User updateState(Long id, UserState state) {
        User user = findById(id);
        user.setState(state);
        log.info("User {} state updated to {}", user.getId(), state);
        return user;
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
        log.info("User deleted = {}", user.getId());
    }
}
