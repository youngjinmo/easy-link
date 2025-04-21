package com.shortenurl.link.repository;

import com.shortenurl.link.domain.Link;
import com.shortenurl.user.domain.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByShortPath(String shortPath);
    boolean existsByShortPath(String shortPath);
    Page<Link> findByUser(User user, Pageable pageable);
} 
