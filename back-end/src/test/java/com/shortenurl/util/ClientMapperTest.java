package com.shortenurl.util;

import com.shortenurl.link_access_log.dto.GeoInfoDto;
import com.shortenurl.link_access_log.dto.UserAgentDto;
import com.shortenurl.stream.dto.AccessLinkLogDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;

import ua_parser.Parser;

import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {

    private MockHttpServletRequest request;
    private ClientMapper clientMapper;

    @BeforeEach
    void setUp() {
        Parser uaParser = new Parser();
        request = new MockHttpServletRequest();
        clientMapper = new ClientMapper(uaParser);
    }

    @Test
    void parseToken() {
        // given
        String mockToken = "test-token";
        request.addHeader("Authorization", "Bearer "+mockToken);

        // when
        String token = clientMapper.parseToken(request);

        // then
        assertEquals(mockToken, token);
    }

    @Test
    void parseClientIp() {
        // given
        String mockIpAddress = " 127.0.0.1";
        request.addHeader("X-Forwarded-For", mockIpAddress);

        // when
        String parsedIpAddress = clientMapper.parseClientIp(request);

        // then
        assertEquals(mockIpAddress, parsedIpAddress);
    }

    @Test
    void parseRequestInfo() {
        // given
        String mockClientIp = "127.0.0.1";
        String mockUAstring = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
        String mockReferer = "http://www.google.com/";

        request.addHeader("X-Forwarded-For", mockClientIp);
        request.addHeader("Referer", mockReferer);
        request.addHeader("User-Agent", mockUAstring);

        AccessLinkLogDto expectedDto = AccessLinkLogDto.from(
                mockClientIp,
                mockReferer,
                UserAgentDto.builder().clientDevice("Other").clientBrowser("Chrome").clientOs("Windows").build(),
                GeoInfoDto.builder().build()
        );

        // when
        AccessLinkLogDto requestDto = clientMapper.parseRequestInfo(request);

        // then
        assertEquals(expectedDto.getClientIp(), requestDto.getClientIp());
        assertEquals(expectedDto.getClientBrowser(), requestDto.getClientBrowser());
        assertEquals(expectedDto.getClientOs(), requestDto.getClientOs());
    }
}