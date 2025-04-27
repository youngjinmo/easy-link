package com.shortenurl.link.service;

import com.shortenurl.link.constant.LinkState;
import com.shortenurl.link.domain.Link;
import com.shortenurl.user.domain.User;

public interface LinkService {
    Link getByPath(String shortPath);
    Link findValidateLinkByPath(String shortPath);
    Link createFreeLink(String originalUrl, String clientIp);
    Link createLink(String originalUrl, User user);
    Link createLink(String originalUrl, String shortPath, User user);
    boolean isExpired(String shortPath);
    void setLinkExpired(Long id);
    void updateState(String shortPath, LinkState state);
    void updateShortPath(String shortPath, String newPath);
    void deleteLink(String shortPath);
    boolean existsShortPath(String shortPath);
}
