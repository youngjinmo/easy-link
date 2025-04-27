package com.shortenurl.link.service;

import com.shortenurl.link.domain.Link;
import com.shortenurl.user.domain.User;
import org.apache.coyote.BadRequestException;

public interface LinkService {
    Link getByPath(String path);
    Link findValidateLinkByPath(String path);
    Link createFreeLink(String originalUrl, String clientIp);
    Link createLink(String originalUrl, User user);
    Link createLink(String originalUrl, String path, User user);
    void setLinkExpired(Long id);
}
