package com.shortenurl.link.domain;

import com.shortenurl.link.constant.LinkState;
import com.shortenurl.user.domain.User;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity(name = "links")
@Getter @Setter
@ToString(exclude = "user")
@NoArgsConstructor
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LinkState state;

    @Column(nullable = false, updatable = false)
    private String originalUrl;

    @Column(nullable = false, unique = true)
    private String shortPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime expiredAt;

    private LocalDateTime deletedAt;

    // 로그인 없이 생성가능한 무료 링크
    public Link(LinkState state, String originalUrl, String shortPath, int expires) {
        this.state = state;
        this.originalUrl = originalUrl;
        this.shortPath = shortPath;
        this.expiredAt = LocalDateTime.now().plusSeconds(expires);
        this.createdAt = LocalDateTime.now();
    }

    // 로그인 한 회원이 이용하는 커스텀 링크
    public Link(LinkState state, String originalUrl, String shortPath, User user) {
        this.state = state;
        this.originalUrl = originalUrl;
        this.shortPath = shortPath;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
