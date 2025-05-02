package com.shortenurl.user.domain;

import com.shortenurl.user.constant.UserProvider;
import com.shortenurl.user.constant.UserState;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState state;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserProvider provider;

    private String providerId;

    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Email user
    public User(String username, UserState state, String name) {
        this.username = username;
        this.name = name;
        this.provider = UserProvider.EMAIL;
    }

    // Oauth user
    public User(String username, UserState state, String name, UserProvider provider, String providerId) {
        this.username = username;
        this.state = state;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
    }
} 
