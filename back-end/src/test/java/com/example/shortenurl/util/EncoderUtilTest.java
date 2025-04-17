package com.example.shortenurl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncoderUtilTest {

    private EncoderUtil encoderUtil;

    @BeforeEach
    void setUp() {
        encoderUtil = new EncoderUtil();
    }

    @Test
    void encode() {
        // given
        String original = "not-encoded-string";

        // when
        String encoded = encoderUtil.encode(original);

        // then
        assertNotEquals(original, encoded);
        assertTrue(encoded.length() > original.length());
        assertEquals(encoderUtil.encode(original), encoded);
    }

    @Test
    void verify() {
        // given
        String original = "not-encoded-string";
        String encoded = encoderUtil.encode(original);

        // then
        assertTrue(encoderUtil.verify(original, encoded));
        assertFalse(encoderUtil.verify("original", encoded));
    }
}