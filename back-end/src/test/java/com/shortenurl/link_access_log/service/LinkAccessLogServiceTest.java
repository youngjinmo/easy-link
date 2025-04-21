package com.shortenurl.link_access_log.service;

import com.shortenurl.stream.constant.AccessLinkLogPolicy;
import com.shortenurl.stream.dto.AccessLinkLogDto;
import com.shortenurl.stream.service.StreamService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkAccessLogServiceTest {

    @Test
    void putRequestInfo() {
        // given
        Long mockLinkId = 1L;
        String mockClientIp = "127.0.0.1";
        String mockClientDevice = "RobotComputer";
        String mockClientBrowser = "Chrome";
        String mockClientOs = "Windows";
        String mockReferer = "https://www.google.com/";
        String mockCountryCode = "US";
        String mockRegionName = "California";

        AccessLinkLogDto dto = new AccessLinkLogDto(mockClientIp, mockClientDevice, mockClientBrowser, mockClientOs, mockCountryCode, mockRegionName, mockReferer);

        StreamService streamService = new FakeStreamService();
        LinkAccessLogService service = new LinkAccessLogService(streamService);

        // when
        service.putRequestInfo(mockLinkId, dto);

        // then
        String key = AccessLinkLogPolicy.STREAM_KEY_PREFIX + ":" + mockLinkId;
        assertEquals(1, streamService.consume(key).size());
    }

    @Test
    void getRequestInfo() {
    }
}