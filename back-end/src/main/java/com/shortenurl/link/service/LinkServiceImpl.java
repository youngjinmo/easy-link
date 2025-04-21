package com.shortenurl.link.service;

import com.shortenurl.cache.service.CacheService;
import com.shortenurl.exception.ExceedFreeLinkLimitException;
import com.shortenurl.exception.InvalidLinkStateException;
import com.shortenurl.exception.LinkExpiredException;
import com.shortenurl.exception.LinkNotFoundException;
import com.shortenurl.link.constant.LinkState;
import com.shortenurl.user.domain.User;
import com.shortenurl.link.domain.Link;
import com.shortenurl.link.repository.LinkRepository;
import com.shortenurl.util.RandomUtilImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.shortenurl.link.constant.LinkPolicy.DEFAULT_SHORT_PATH_LENGTH;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkServiceImpl {

    private final LinkRepository linkRepository;
    private final CacheService cacheService;
    private final RandomUtilImpl randomUtilImpl;

    @Value("${app.free-link.duration}")
    private int freeLinkExpires;

    @Value("${app.free-link.max-access}")
    private int freeLinkMaxHits;

    @Transactional(readOnly = true)
    public Link getByPath(String shortPath) {
        return linkRepository.findByShortPath(shortPath).orElseThrow(
                () -> new LinkNotFoundException()
        );
    }

    @Transactional
    public Link findValidateLinkByPath(String shortPath) {
        Link link = getByPath(shortPath);
        if (isExpired(shortPath)) {
            throw new LinkExpiredException();
        }
        if (hasReachedMaxAccessHits(shortPath)) {
            throw new ExceedFreeLinkLimitException();
        }
        if (isInvalidateState(link.getState())) {
            throw new InvalidLinkStateException();
        }
        return link;
    }

    @Transactional
    public Link createFreeLink(String originalUrl, String clientIp) {
        // ip 기반으로 free link 생성 여부
        if(cacheService.hasFreeLink(clientIp)) {
            //  1. 이미 생성 이력있다면, throw 403
            throw new ExceedFreeLinkLimitException();
        }

        //  2. 생성 이력없다면, free link 생성
        String randomShortenUrl = generateRandomPath(DEFAULT_SHORT_PATH_LENGTH);
        Link link = new Link(LinkState.NORMAL, originalUrl, randomShortenUrl, freeLinkMaxHits, freeLinkExpires);
        Link entity = linkRepository.save(link);
        log.info("Free link created free link: {}", link);

        // free link 생성 이력 캐싱
        cacheService.setFreeLink(clientIp, entity.getId());

        return entity;
    }

    @Transactional
    public Link createShortLink(String originalUrl, User user) {
        // 사용자가 자동으로 생성된 랜덤 url path를 원할때
        String shortPath = generateRandomPath(DEFAULT_SHORT_PATH_LENGTH);

        Link link = new Link(LinkState.NORMAL, originalUrl, shortPath, user);
        link = linkRepository.save(link);
        log.info("Link created link by user id={}: {}", user.getId(), link);

        return link;
    }

    @Transactional
    public Link createShortLink(String originalUrl, String shortPath, User user) {
        Link link = new Link(LinkState.NORMAL, originalUrl, shortPath, user);
        link = linkRepository.save(link);
        log.info("Link created link by user id={}: {}", user.getId(), link);

        return link;
    }

    @Transactional
    public boolean isExpired(String shortPath) {
        Link link = getByPath(shortPath);
        // 만료 상태면 반환
        if (link.getState().equals(LinkState.EXPIRED)) {
            return true;
        }
        // 만료 상태는 아니지만, 만료일이 지난 링크라면 상태 변환
        if (LocalDateTime.now().isAfter(link.getExpiredAt())) {
            link.setExpiredAt(LocalDateTime.now());
            log.info("Link {} expired", link.getShortPath());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean hasReachedMaxAccessHits(String shortPath) {
        Link link = getByPath(shortPath);
        if (link.getCurrentHits() >= link.getMaxHits()) {
            link.setExpiredAt(LocalDateTime.now());
            log.info("Link {} expired by excess max access hits", shortPath);
            return true;
        }
        return false;
    }

    @Transactional
    public void incrementAccessHits(String shortPath, int increment) {
        Link link = linkRepository.findByShortPath(shortPath).orElseThrow(
                () -> new LinkNotFoundException()
        );
        link.setCurrentHits(link.getCurrentHits() + increment);
    }

    @Transactional
    public void updateLinkState(String shortPath, LinkState state) {
        Link link = getByPath(shortPath);
        LinkState previousState = link.getState();
        link.setState(state);
        log.info("Link {} state updated to {}", previousState, state);
    }

    @Transactional
    public void updateLinkShortPath(String shortPath, String newShortPath) {
        Link link = getByPath(shortPath);
        link.setShortPath(newShortPath);
        log.info("Link {} short path updated to {}", shortPath, newShortPath);
    }

    @Transactional
    public void deleteLink(String shortPath) {
        Link link = getByPath(shortPath);
        LinkState previousState = link.getState();
        updateLinkState(shortPath, LinkState.DELETED);
        log.info("Link {} deleted from {}", shortPath, previousState);
    }

    private String generateRandomPath(int length) {
        final int MAX_TRY_COUNT = 5;

        int try_count = 0;
        String randomPath = randomUtilImpl.generate(length);

        while (try_count < MAX_TRY_COUNT && linkRepository.existsByShortPath(randomPath)) {
            ++try_count;
            randomPath = randomUtilImpl.generate(length);
        }

        // 무한하게 조회하지 않도록..
        if (try_count >= MAX_TRY_COUNT) {
            randomPath += "1";
        }

        return randomPath;
    }

    private boolean isInvalidateState(LinkState state) {
        return Arrays.asList(LinkState.DELETED, LinkState.EXPIRED, LinkState.HIDDEN).contains(state);
    }
}
