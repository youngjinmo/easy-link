package com.shortenurl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncoderUtilImplTest {

    private EncoderUtilImpl encoderUtilImpl;

    @BeforeEach
    void setUp() {
        encoderUtilImpl = new EncoderUtilImpl();
    }

    @Test
    void encode() {
        // given
        String original = "not-encoded-string";

        // when
        String encoded = encoderUtilImpl.encode(original);

        // then
        assertNotEquals(original, encoded);
        assertTrue(encoded.length() > original.length());
        assertEquals(encoderUtilImpl.encode(original), encoded);
    }

    @Test
    void decode() {
        // given
        String original = "not-encoded-string";
        String encoded = encoderUtilImpl.encode(original);

        // when
        String decoded = encoderUtilImpl.decode(encoded);

        // then
        assertEquals(original, decoded);
        assertNotEquals(encoded, decoded);
    }

    @Test
    void verify() {
        // given
        String original = "not-encoded-string";
        String encoded = encoderUtilImpl.encode(original);

        // then
        assertTrue(encoderUtilImpl.verify(original, encoded));
        assertFalse(encoderUtilImpl.verify("original", encoded));
    }
}
