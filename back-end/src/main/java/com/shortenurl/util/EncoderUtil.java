package com.shortenurl.util;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public final class EncoderUtil {

    private final String HASH_ALGORITHM = "SHA-256";

    public String encode(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("At least one value must be provided");
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashed = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashed) {
                String hex = String.format("%02x", 0xff & b);
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("hash algorithm is wrong: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean verify(String targetString, String encodedString) {
        return encodedString.equals(encode(targetString));
    }
} 