package com.shortenurl.util;

import com.shortenurl.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import java.util.Base64;

@Slf4j
@Component
public final class EncoderUtilImpl implements EncodeUtil {
    public String encode(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("At least one value must be provided");
        }

        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public String encode(String value, String salt) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("At least one value must be provided");
        }

        return Base64.getEncoder().encodeToString((salt + value).getBytes(StandardCharsets.UTF_8));
    }

    public String decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            throw new BadRequestException("At least one value must be provided");
        }

        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public boolean verify(String targetString, String encodedString) {
        return encodedString.equals(encode(targetString));
    }
}
