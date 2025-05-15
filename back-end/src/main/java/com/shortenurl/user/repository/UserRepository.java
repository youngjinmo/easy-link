package com.shortenurl.user.repository;

import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderId(UserProvider provider, String providerId);
    Optional<User> findByUsername(String username);
}
