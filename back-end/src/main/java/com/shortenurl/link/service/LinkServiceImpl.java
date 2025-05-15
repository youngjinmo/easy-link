package com.shortenurl.link.service;

import com.shortenurl.cache.service.CacheService;
import com.shortenurl.exception.*;
import com.shortenurl.link.constant.LinkState;
import com.shortenurl.user.domain.User;
import com.shortenurl.link.domain.Link;
import com.shortenurl.link.repository.LinkRepository;
import com.shortenurl.util.RandomUtil;

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
public class LinkServiceImpl implements LinkService {

    private final LinkRepository linkRepository;
    private final CacheService cacheService;
    private final RandomUtil randomUtilImpl;

    @Value("${app.free-link.duration}")
    private int freeLinkExpires;

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
        Link link = new Link(LinkState.NORMAL, originalUrl, randomShortenUrl, freeLinkExpires);
        Link entity = linkRepository.save(link);
        log.info("Free link created free link: {}", link);

        // free link 생성 이력 캐싱
        cacheService.setFreeLink(clientIp, entity.getId());

        return entity;
    }


    /**
     * 사용자가 자동으로 생성된 램덤 URL path로 링크 생성
     * @param originalUrl
     * @param user
     * @return Link
     */
    @Transactional
    public Link createLink(String originalUrl, User user) {
        String shortPath = generateRandomPath(DEFAULT_SHORT_PATH_LENGTH);
        Link link = new Link(LinkState.NORMAL, originalUrl, shortPath, user);
        link = linkRepository.save(link);
        log.info("Link created link by user id={}: {}", user.getId(), link);

        return link;
    }

    /**
     *  사용자가 임의로 입력한 path로 링크 생성
     * @param originalUrl
     * @param shortPath
     * @param user
     * @return
     */
    @Transactional
    public Link createLink(String originalUrl, String shortPath, User user) {
        if (linkRepository.existsByShortPath(shortPath)) {
            throw new DuplicateLinkPathException();
        }
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
    public void setLinkExpired(Long linkId) {
        Link link = linkRepository.findById(linkId).orElseThrow(
                () -> new LinkNotFoundException()
        );
        link.setExpiredAt(LocalDateTime.now());
        log.info("Link {} expired", link.getShortPath());
    }

    @Transactional
    public void updateState(String shortPath, LinkState state) {
        Link link = getByPath(shortPath);
        LinkState previousState = link.getState();
        link.setState(state);
        log.info("Link {} state updated to {}", previousState, state);
    }

    @Transactional
    public void updateShortPath(String shortPath, String newShortPath) {
        Link link = getByPath(shortPath);
        link.setShortPath(newShortPath);
        log.info("Link {} short path updated to {}", shortPath, newShortPath);
    }

    @Transactional
    public void deleteLink(String shortPath) {
        Link link = getByPath(shortPath);
        LinkState previousState = link.getState();
        updateState(shortPath, LinkState.DELETED);
        log.info("Link {} deleted from {}", shortPath, previousState);
    }

    @Transactional(readOnly = true)
    public boolean existsShortPath(String shortPath) {
        return linkRepository.existsByShortPath(shortPath);
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
