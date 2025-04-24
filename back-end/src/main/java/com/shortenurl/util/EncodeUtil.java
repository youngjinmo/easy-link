package com.shortenurl.util;

public interface EncodeUtil {
    String encode(String raw);
    boolean verify(String raw, String encoded);
}
