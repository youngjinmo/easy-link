package com.shortenurl.util;

public interface EncodeUtil {
    String encode(String raw);
    String encode(String raw, String salt);
    String decode(String encoded);
    boolean verify(String raw, String encoded);
}
