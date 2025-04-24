package com.shortenurl.util;

import com.shortenurl.util.ClientMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;

import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {

    @Mock
    private MockHttpServletRequest request;

    private ClientMapper clientMapper;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        clientMapper = new ClientMapper();
    }

    @Test
    void getIpAddress() {
        // given
        String mockIpAddress = " 127.0.0.1";
        request.addHeader("X-Forwarded-For", mockIpAddress);

        // when
        String parsedIpAddress = clientMapper.getIpAddress(request);

        // then
        assertEquals(mockIpAddress, parsedIpAddress);
    }

    @Test
    void getUserAgent_with_normal_user_agent() {
        // given
        String mockUserAgent = "UglyFox";
        request.addHeader("User-Agent", mockUserAgent);

        // when
        String parsedUserAgent = clientMapper.getUserAgent(request);

        // then
        assertEquals(mockUserAgent, parsedUserAgent);
    }

    @Test
    void getUserAgent_with_abnormal_user_agent() {
        // given
        request.addHeader("User-Agent", "");

        // when
        String parsedUserAgent = clientMapper.getUserAgent(request);

        // then
        assertEquals("unknown", parsedUserAgent);
    }
}